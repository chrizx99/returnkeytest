package com.example.demo.controllers

import com.example.demo.helpers.EncoderHelper
import com.example.demo.helpers.OrdersHelper
import com.example.demo.helpers.ReturnsHelper
import com.example.demo.models.ReturnsItemModel
import com.example.demo.models.ReturnsItemStatus
import com.example.demo.models.ReturnsModel
import com.example.demo.repositories.*
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

data class ReturnsCreateItemReq(
	val sku: String,
	val qty: Long,
)

data class ReturnsCreateReq(
	val token: String,
	val items: List<ReturnsCreateItemReq>,
)

data class ReturnsCreateDataRes(
	val id: String,
	val amount: Float,
)

data class ReturnsCreateRes(
	var success: Boolean = false,
	var msg: String? = null,
	var data: ReturnsCreateDataRes? = null,
)

data class ReturnsViewItemRes(
	val returnsItemId: String,
	val itemName: String,
	val qty: Long,
	val price: Float,
	val status: String,
)

data class ReturnsViewDataRes(
	val status: String,
	val items: List<ReturnsViewItemRes>,
	val amount: Float,
)

data class ReturnsViewRes(
	var success: Boolean = false,
	var msg: String? = null,
	var data: ReturnsViewDataRes? = null
)

data class ReturnsUpdateItemStatusReq(
	val status: String,
)

data class ReturnsUpdateItemStatusRes(
	var success: Boolean = false,
	var msg: String? = null,
)

@RestController
@RequestMapping("returns")
class ReturnsController(
	val ordersRepository: OrdersRepository,
	val returnsRepository: ReturnsRepository,
	val returnsItemRepository: ReturnsItemRepository
) {
	@PostMapping
	fun create(@RequestBody returnCreateReq: ReturnsCreateReq): ReturnsCreateRes {
		val res = ReturnsCreateRes()

		// exit because no Item in the param
		if (returnCreateReq.items.toList().isEmpty()) {
			res.msg = "Requires at least 1 item"
			return res
		}

		val tokens = returnCreateReq.token.split("%%%")

		// exit because invalid Token format
		if (tokens.size != 2) {
			res.msg = "Invalid Token"
			return res
		}

		val orderId: String = EncoderHelper.decode(tokens[0])
		val emailAddress: String = EncoderHelper.decode(tokens[1])

		val availableItemCountList: List<OrdersGetAvailableItemCountRes> = ordersRepository.getAvailableItemCount(orderId, emailAddress)

		// exit because no Order from given Token
		if (availableItemCountList.isEmpty()) {
			res.msg = OrdersHelper.ResultMessages.NO_ORDER.value
			return res
		}

		// exit because there is no Item available to be returned
		if (!OrdersHelper.hasReturnableItem(availableItemCountList)) {
			res.msg = OrdersHelper.ResultMessages.NO_AVAILABLE_ITEM.value
			return res
		}

		// stores maximum amount from this returns
		var amount: Float = 0f

		// validate Items
		for (item in returnCreateReq.items) {
			if (item.qty < 1) {
				res.msg = "There is item with quantity less than or equal to 0"
				return res
			}

			// flag to indicate if Item exist at Order
			var match: Boolean = false
			for (availableItemCount in availableItemCountList) {
				// continue check to next item on different SKU
				if (item.sku != availableItemCount.sku) {
					continue
				}

				match = true

				// exit because SKU already returned before
				if (availableItemCount.qty < 1) {
					res.msg = "There is item that already returned before"
					return res
				}

				// exit becuase returned qty bigger than order's
				if (availableItemCount.qty < item.qty) {
					res.msg = "There is item with invalid quantity amount"
					return res
				}

				// add the amount from SKU to total SKU
				amount += availableItemCount.price * item.qty

				break;
			}

			// exit because SKU not exist at Order
			if (!match) {
				res.msg = "Item not found"
				return res
			}
		}

		// insert to Returns
		val returns = returnsRepository.save(
			ReturnsModel(
				orderId = orderId,
				emailAddress = emailAddress
			)
		)

		// insert Items to ReturnsItem
		for (item in returnCreateReq.items) {
			returnsItemRepository.save(
				ReturnsItemModel(
					returnsId = returns.returnsId,
					sku = item.sku,
					quantity = item.qty
				)
			)
		}

		res.success = true
		res.data = ReturnsCreateDataRes(
			returns.returnsId.toString(),
			amount
		)
		return res
	}

	@GetMapping("{id}")
	fun view(@PathVariable id: Long): ReturnsViewRes {
		val res = ReturnsViewRes()

		val returns: Optional<ReturnsModel> = returnsRepository.findById(id)
		// exit because no Returns from given param
		if (returns.isEmpty) {
			res.msg = ReturnsHelper.ResultMessages.NO_RETURNS.value
			return res
		}

		val returnItems: Iterable<ReturnsGetWithDataRes> = returnsItemRepository.getWithData(id)

		// stores Item Data to be returned
		val returnItemData: ArrayList<ReturnsViewItemRes> = arrayListOf()
		var returnStatus: String = "COMPLETE"
		// stores maximum available amount. Only calculates non Rejected submission
		var amount: Float = 0f

		for (returnItem in returnItems) {
			// insert to variable to be returned
			returnItemData.add(
				ReturnsViewItemRes(
					returnItem.returnsItemId.toString(),
					returnItem.name,
					returnItem.qty,
					returnItem.price,
					returnItem.status,
				)
			)

			// update Returns status to pending it there's pending Item Return
			if (returnItem.status == ReturnsItemStatus.PENDING.value) {
				returnStatus = "AWAITING_APPROVAL"
			}

			// includes the Item amount only if its status is not Rejected
			if (returnItem.status != ReturnsItemStatus.REJECTED.value) {
				amount += returnItem.qty * returnItem.price
			}
		}

		res.success = true
		res.data = ReturnsViewDataRes(
			returnStatus,
			returnItemData,
			amount,
		)
		return res
	}

	@PutMapping("{id}/items/{itemId}/qc/status")
	fun updateItemStatus(@PathVariable id: Long, @PathVariable itemId: Long, returnUpdateItemStatusReq: ReturnsUpdateItemStatusReq): ReturnsUpdateItemStatusRes {
		val res = ReturnsUpdateItemStatusRes()

		// validate status value
		try {
			// exit because status value is PENDING
			if (ReturnsItemStatus.valueOf(returnUpdateItemStatusReq.status) == ReturnsItemStatus.PENDING) {
				res.msg = "Invalid status' value"
				return res
			}
		} catch (e: IllegalArgumentException) {
			// exit because status value is invalid(not ACCEPTED / REJECTED)
			res.msg = "Invalid status' value"
			return res
		}

		val returns: Optional<ReturnsModel> = returnsRepository.findById(id.toLong())
		// exit because no Returns from given param
		if (returns.isEmpty) {
			res.msg = ReturnsHelper.ResultMessages.NO_RETURNS.value
			return res
		}

		val returnItemRes: Optional<ReturnsItemModel> = returnsItemRepository.findById(itemId.toLong())
		// exit because no Returns Item from given param
		if (returnItemRes.isEmpty) {
			res.msg = "Returned Item not found"
			return res
		}

		val returnItem: ReturnsItemModel = returnItemRes.get();
		// exit because no Returns ID from Returns Item Data doesn't match with Returns ID from param
		if (returnItem.returnsId != id) {
			res.msg = "Returned Item not found"
			return res
		}

		returnItem.status = returnUpdateItemStatusReq.status

		returnsItemRepository.save(returnItem)

		res.success = true
		return res
	}
}