package com.example.demo.repositories

import com.example.demo.models.OrdersModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface OrdersGetAvailableItemCountRes {
	val sku: String
	val qty: Long
	val price: Float
}

@Repository
interface OrdersRepository: JpaRepository<OrdersModel, Long> {
	@Query(
		(
			"SELECT o.SKU AS sku, (CASE SUM(ri.QUANTITY) WHEN > 0 THEN 0 ELSE SUM(o.QUANTITY) END) AS qty, o.price as price " +
			"FROM ORDERS o " +
				"LEFT JOIN RETURNS r ON o.ORDER_ID = r.ORDER_ID AND o.EMAIL_ADDRESS = r.EMAIL_ADDRESS " +
				"LEFT JOIN RETURNS_ITEM ri ON r.RETURNS_ID = ri.RETURNS_ID AND o.SKU = ri.SKU " +
			"WHERE " +
				"o.ORDER_ID = :orderId AND " +
				"o.EMAIL_ADDRESS = :email " +
			"GROUP BY o.ORDER_ID, o.EMAIL_ADDRESS, o.SKU"
		),
		nativeQuery = true
	)
	fun getAvailableItemCount(@Param("orderId") orderId: String, @Param("email")  email: String): List<OrdersGetAvailableItemCountRes>

	fun findByOrderIdAndEmailAddress(orderId: String, emailAddress: String): List<OrdersModel>
}