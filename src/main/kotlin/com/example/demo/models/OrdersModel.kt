package com.example.demo.models

import javax.persistence.*

@Entity
@Table(name = "ORDERS")
data class OrdersModel(
	@Id
	val dbId: Long = 0,
	val orderId: String,
	val emailAddress: String,
	val sku: String,
	val quantity: Long,
	val price: Float,
	val itemName: String
)