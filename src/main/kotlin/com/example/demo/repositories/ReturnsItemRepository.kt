package com.example.demo.repositories

import com.example.demo.models.ReturnsItemModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface ReturnsGetWithDataRes {
	val returnsItemId: Long
	val sku: String
	val name: String
	val qty: Long
	val price: Float
	val status: String
}

@Repository
interface ReturnsItemRepository: JpaRepository<ReturnsItemModel, Long> {
	@Query(
		(
			"SELECT ri.RETURNS_ITEM_ID AS returnsItemId, o.SKU AS sku, o.ITEM_NAME AS name, ri.QUANTITY AS qty, o.PRICE AS price, ri.STATUS AS status " +
			"FROM RETURNS_ITEM ri " +
				"INNER JOIN RETURNS r ON r.RETURNS_ID = ri.RETURNS_ID " +
				"INNER JOIN ORDERS o ON o.ORDER_ID = r.ORDER_ID AND o.EMAIL_ADDRESS = r.EMAIL_ADDRESS AND o.SKU = ri.SKU " +
			"WHERE " +
				"ri.RETURNS_ID = :returnsId"
		),
		nativeQuery = true
	)
	fun getWithData(@Param("returnsId") returnsId: Long): List<ReturnsGetWithDataRes>
}