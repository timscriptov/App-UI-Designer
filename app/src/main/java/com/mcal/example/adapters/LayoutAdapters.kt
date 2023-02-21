package com.mcal.example.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcal.example.R
import com.mikepenz.fastadapter.items.AbstractItem

open class LayoutAdapters : AbstractItem<LayoutAdapters.ViewHolder>() {
    var itemTitle: String? = null

    /** The type of the Item. Can be a hardcoded INT, but preferred is a defined id */
    override val type: Int
        get() = R.id.main_menu_container

    /** The layout for the given item */
    override val layoutRes: Int
        get() = R.layout.item_main_list

    fun withId(id: Long): LayoutAdapters {
        this.identifier = id
        return this
    }

    fun withTitle(title: String): LayoutAdapters {
        this.itemTitle = title
        return this
    }


    /** Binds the data of this item onto the viewHolder */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        itemTitle?.let {
            holder.title.text = it
        }
    }

    /** View needs to release resources when its recycled */
    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.title.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.menu_title)
    }
}