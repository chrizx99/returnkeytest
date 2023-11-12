package com.example.demo.models

import javax.persistence.*

@Entity
@Table(name = "RETURNS")
data class ReturnsModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val returnsId: Long = 0,
	val orderId: String,
	val emailAddress: String
)