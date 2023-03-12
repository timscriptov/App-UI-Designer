package com.mcal.example.utils

import com.mcal.example.data.Const

object Utils {
    fun isAndroidXLayout(layoutName: String): Boolean {
        Const.layout.forEach { name ->
            if (layoutName.contains(name)) {
                return true
            }
        }
        return false
    }
}