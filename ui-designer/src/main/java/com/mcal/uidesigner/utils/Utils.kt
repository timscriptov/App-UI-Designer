package com.mcal.uidesigner.utils

import android.R.color
import android.R.drawable
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mcal.uidesigner.XmlLayoutResourceFinder
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object Utils {
    @JvmStatic
    fun getUnit(context: Context, finder: XmlLayoutResourceFinder, reference: String): Int {
        var v = 0f
        if (!(reference.startsWith("@") || reference.startsWith("?") || reference.startsWith("wrap_content") || reference.startsWith(
                "match_parent"
            ) || reference.startsWith("fill_parent"))
        ) {
            v = reference.substring(0, reference.length - 2).toFloat()
        }
        if (reference.startsWith("@android:dimen/")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                getDimen(reference).toFloat(),
                context.resources.displayMetrics
            ).toInt()
        } else if (reference.startsWith("@dimen/")) {
            val value = finder.findUserResourceValue(reference)
            if (value != null) {
                return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    value.substring(0, value.length - 2).toFloat(),
                    context.resources.displayMetrics
                ).toInt()
            }
        } else if (reference.endsWith("sp")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                v,
                context.resources.displayMetrics
            ).toInt()
        } else if (reference.endsWith("dp") || reference.endsWith("dip")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                context.resources.displayMetrics
            ).toInt()
        } else if (reference.endsWith("pt")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PT,
                v,
                context.resources.displayMetrics
            ).toInt()
        } else if (reference.endsWith("px")) {
            return v.toInt()
        } else if (reference.endsWith("mm")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                v,
                context.resources.displayMetrics
            ).toInt()
        } else if (reference.endsWith("in")) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN,
                v,
                context.resources.displayMetrics
            ).toInt()
        }
        return -1
    }

    @JvmStatic
    fun getDrawable(context: Context, finder: XmlLayoutResourceFinder, value: String): Drawable? {
        if (isColor(value)) {
            return ColorDrawable(Color.parseColor(value))
        }
        val name = parseReferName(value)
        if (value.startsWith("@android:drawable/")) {
            val id = getSystemResourceId(drawable::class.java, name)
            if (id != -1) {
                return ResourcesCompat.getDrawable(context.resources, id, context.theme)
            }
        } else if (value.startsWith("@drawable/")) {
            return finder.findUserDrawable(value)
        } else if (value.startsWith("@android:color/")) {
            val id = getSystemResourceId(color::class.java, name)
            if (id != -1) {
                return ColorDrawable(ContextCompat.getColor(context, id))
            }
        } else if (value.startsWith("@color/")) {
            return ColorDrawable(Color.parseColor(value))
        } else if (value.startsWith("?android:attr/")) {
            val i = getAttr(value)
            val a = context.obtainStyledAttributes(intArrayOf(i))
            val d = a.getDrawable(a.getIndex(0))
            a.recycle()
            return d
        }
        return null
    }

    @JvmStatic
    fun getColor(context: Context, finder: XmlLayoutResourceFinder, value: String): Int {
        if (isColor(value)) {
            return Color.parseColor(value)
        }
        val name = parseReferName(value)
        if (value.startsWith("@android:color/")) {
            val id = getSystemResourceId(color::class.java, name)
            if (id != -1) {
                return ContextCompat.getColor(context, id)
            }
        } else if (value.startsWith("@color/")) {
            val color = finder.findResourcePropertyValue(value)
            if (color != null) {
                if (isColor(color)) {
                    return Color.parseColor(color)
                }
            }
        }
        return -1
    }

    @JvmStatic
    fun getDimen(style: String): Int {
        if (style.startsWith("@android:dimen/")) {
            val name = parseReferName(style)
            return getSystemResourceId(style::class.java, name.replace(".", "_"))
        }
        return -1
    }

    @JvmStatic
    fun getStyle(style: String): Int {
        if (style.startsWith("@android:style/")) {
            val name = parseReferName(style)
            return getSystemResourceId(style::class.java, name.replace(".", "_"))
        }
        return -1
    }

    @JvmStatic
    fun getAttr(attr: String): Int {
        val name = if (attr.startsWith("?android:attr/")) {
            parseReferName(attr)
        } else if (attr.startsWith("?android:")) {
            parseReferName(attr, ":")
        } else if (attr.startsWith("@android:attr/")) {
            parseReferName(attr)
        } else if (attr.startsWith("?attr/android:")) {
            parseReferName(attr, ":")
        } else {
            return -1
        }
        return getSystemResourceId(attr::class.java, name)
    }

    @JvmStatic
    fun isColor(color: String): Boolean {
        return color.matches(Regex("^#([\\da-fA-F]{6}|[\\da-fA-F]{8})$"))
    }

    @JvmStatic
    fun parseReferName(reference: String): String {
        return parseReferName(reference, "/")
    }

    @JvmStatic
    fun parseReferName(reference: String, sep: String): String {
        return reference.substring(reference.indexOf(sep) + 1)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun transfer(inputStream: InputStream, out: OutputStream): Long {
        var size: Long = 0
        val buf = ByteArray(1000)
        while (true) {
            val c = inputStream.read(buf)
            if (c != -1) {
                size += c.toLong()
                out.write(buf, 0, c)
            } else {
                inputStream.close()
                out.close()
                return size
            }
        }
    }

    @JvmStatic
    fun findLayoutFiles(resDirPath: String): MutableList<File> {
        val layoutFiles: MutableList<File> = ArrayList()
        File(resDirPath, "layout").walk().forEach { file ->
            val name = file.name
            if (name.endsWith(".xml")) {
                layoutFiles.add(file)
            }
        }
        layoutFiles.sortWith(Comparator.comparing { obj: File -> obj.name })
        return layoutFiles
    }

    @JvmStatic
    fun toHexColor(color: Int): String {
        return if (Color.alpha(color) == 255) {
            String.format("#%06X", 0xFFFFFF and color)
        } else String.format("#%08X", color)
    }

    @JvmStatic
    fun getSystemResourceId(clazz: Class<*>, name: String): Int {
        try {
            return clazz.getField(name).getInt(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}