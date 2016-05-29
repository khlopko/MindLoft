package com.aavens.mindloft.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.OnRoomClickListener
import com.aavens.mindloft.managers.RoomsManager

class RoomRecycleViewAdapter(listener: OnRoomClickListener?):
        RecyclerView.Adapter<RoomRecycleViewAdapter.ViewHolder>() {

    private var listener: OnRoomClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val room = RoomsManager.instance[position]
        val text = room.title
        holder?.titleView?.text = text
        holder?.view?.setOnClickListener( { view: View -> listener?.onItemClick(holder.adapterPosition) })
        holder?.view?.setOnLongClickListener(holder);
    }

    override fun getItemCount(): Int {
        return RoomsManager.instance.count;
    }

    // MARK: ViewHolder Class

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnLongClickListener {

        val view: View = view
        val titleView: TextView

        init {
            titleView = view.findViewById(R.id.info_text) as TextView
        }

        override fun onLongClick(view: View): Boolean {
            listener?.onItemLongClick(adapterPosition);
            return false;
        }

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'";
        }
    }
}