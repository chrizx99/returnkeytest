package com.example.demo.models

import javax.persistence.*

enum class ReturnsItemStatus(val value: String) {
	PENDING("PENDING"),
	ACCEPTED("ACCEPTED"),
	REJECTED("REJECTED"),
}

@Entity
@Table(name = "RETURNS_ITEM")
data class ReturnsItemModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val returnsItemId: Long = 0,
	val returnsId: Long,
	val sku: String,
	val quantity: Long,
	var status: String = ReturnsItemStatus.PENDING.value
)