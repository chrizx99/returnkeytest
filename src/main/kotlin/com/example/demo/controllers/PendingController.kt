package com.example.demo.controllers

import com.example.demo.helpers.EncoderHelper
import com.example.demo.helpers.OrdersHelper
import com.example.demo.repositories.OrdersGetAvailableItemCountRes
import com.example.demo.repositories.OrdersRepository
import org.springframework.web.bind.annotation.*

data class PendingReturnReq (
	val orderId: String,
	val emailAddress: String
)

data class PendingReturnRes (
	var success: Boolean = false,
	var msg: String? = null,
	var token: String? = null,
)

@RestController
@RequestMapping("pending")
class PendingController(
	val ordersRepository: OrdersRepository
) {
	@PostMapping("/returns")
	fun returns(@RequestBody pendingReturnReq: PendingReturnReq): PendingReturnRes {
		val res = PendingReturnRes()

		val availableItemCount: List<OrdersGetAvailableItemCountRes> = ordersRepository.getAvailableItemCount(pendingReturnReq.orderId, pendingReturnReq.emailAddress)

		// exit because no Order from given params
		if (availableItemCount.isEmpty()) {
			res.msg = OrdersHelper.ResultMessages.NO_ORDER.value
			return res
		}

		// exit because there is no Item available to be returned
		if (!OrdersHelper.hasReturnableItem(availableItemCount)) {
			res.msg = OrdersHelper.ResultMessages.NO_AVAILABLE_ITEM.value
			return res
		}

		res.success = true
		res.token = EncoderHelper.encode(pendingReturnReq.orderId) + "%%%" + EncoderHelper.encode(pendingReturnReq.emailAddress)
		return res
	}
}