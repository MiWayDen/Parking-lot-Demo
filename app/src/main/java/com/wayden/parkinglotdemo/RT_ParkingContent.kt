package com.wayden.parkinglotdemo

class RTparking : ArrayList<RTparkingItem>()

data class RTparkingItem(
    val ID: Int,
    val Name: String,
    val X: Double,
    val Y: Double,
    val ParkingLots: List<ParkingLot>
)


data class ParkingLot(
    val ID: String,
    val Position: String,
    val X: Double,
    val Y: Double,
    val TotalCar: Int,
    val AvailableCar: Int,
    val AvailableCarRGB: String,
    val Notes: String,
    val Type: String,
    val Updatetime: String,
)
data class ParkingList(
    val Area : MutableList<String> = mutableListOf("","123")
)