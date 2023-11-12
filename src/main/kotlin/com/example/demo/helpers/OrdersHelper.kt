package com.example.demo.helpers

import com.example.demo.repositories.OrdersGetAvailableItemCountRes

object OrdersHelper {
	enum class ResultMessages(val value: String) {
		NO_ORDER("Order not found"),
		NO_AVAILABLE_ITEM("No item available for return")
	}

	fun hasReturnableItem(availableItemCountRes: List<OrdersGetAvailableItemCountRes>): Boolean {
		val availableItemCountList: List<OrdersGetAvailableItemCountRes> = availableItemCountRes.toList()
		// Return true if there's returnable items
		for (availableItemCount in availableItemCountList) {
			if (availableItemCount.qty > 0) {
				return true
			}
		}
		return false
	}
}