package com.wayden.parkinglotdemo

class ParkingContent : ArrayList<ParkingContentItem>()

data class ParkingContentItem(
    val ID: String,
    val Position: String,
    val X: String,
    val Y: String,
    val AvailableCarRGB: String
)