package com.routeplanner.app.core.utils

import com.swmansion.kmpmaps.core.Coordinates

object PolylineDecoder {
    fun decode(encoded: String): List<Coordinates> {
        val result = mutableListOf<Coordinates>()
        var index  = 0
        var lat    = 0
        var lng    = 0

        while (index < encoded.length) {
            var shift  = 0
            var result1 = 0
            var b: Int
            do {
                b = encoded[index++].code - 63
                result1 = result1 or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result1 and 1 != 0) (result1 shr 1).inv() else result1 shr 1
            lat += dlat

            shift   = 0
            result1 = 0
            do {
                b = encoded[index++].code - 63
                result1 = result1 or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result1 and 1 != 0) (result1 shr 1).inv() else result1 shr 1
            lng += dlng

            result.add(Coordinates(lat / 1E5, lng / 1E5))
        }
        return result
    }
}