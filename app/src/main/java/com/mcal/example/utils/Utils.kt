package com.mcal.example.utils

import com.mcal.example.data.Const

object Utils {
    fun isAndroidXLayout(layoutName: String): Boolean {
        val regex =
            Regex("""^int (\S+) (\S+)\s+([\dx]+)$|^int (\S+) (\S+) (\S+)\s+([\dx]+)$|^int (\S+) (\S+) (\S+) (\S+)\s+([\dx]+)$""")

        Const.layout.forEach { line ->
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val name = matchResult.groupValues[2]
                if (layoutName.contains(name)) {
                    return true
                }
            }
        }
        return false
    }
}