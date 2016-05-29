package com.aavens.mindloft.ui.detail

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.OnThingClickListener
import com.aavens.mindloft.managers.ThingsManager
import com.aavens.mindloft.models.Thing

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.Observer

class DetailRoomRecyclerViewAdapter(private val context: Context,
                                    private val listener: OnThingClickListener,
                                    observer: Observer):
        RecyclerView.Adapter<DetailRoomRecyclerViewAdapter.ViewHolder>() {

    private val manager = ThingsManager.instance

    init {
        manager.addObserver(observer)
    }

    fun addThingWithType(roomId: Long, type: Thing.Type, value: String) {
        val thing = Thing(type, value, roomId)
        manager.append(thing)
    }

    fun removeThingAtIndex(index: Int) {
        manager.removeAtIndex(index)
    }

    // MARK: Override

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val type = Thing.Type.values()[viewType]
        var id = 0
        when (type) {
            Thing.Type.TEXT -> id = R.layout.text_item
            Thing.Type.LINK -> id = R.layout.link_item
            Thing.Type.IMAGE -> id = R.layout.image_item
        }
        val view = LayoutInflater.from(parent.context).inflate(id, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mView.setOnLongClickListener(holder)
        val type = manager[position].type
        when (type) {
            Thing.Type.TEXT -> bindTextView(position, holder)
            Thing.Type.LINK -> bindLinkView(position, holder)
            Thing.Type.IMAGE -> bindImageView(position, holder)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return manager.get(position).type.ordinal
    }

    override fun getItemCount(): Int {
        return manager.count
    }

    // MARK: ViewHolder Class

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView), View.OnLongClickListener {
        val editText: EditText?
        val button: ImageButton?
        val textView: TextView?
        val imageView: ImageView?

        init {
            editText = mView.findViewById(R.id.thing_text) as? EditText
            textView = mView.findViewById(R.id.link_text) as? TextView
            imageView = mView.findViewById(R.id.image_view) as? ImageView
            button = mView.findViewById(R.id.remove_text_item_button) as? ImageButton
        }

        override fun onLongClick(v: View): Boolean {
            listener.onItemLongClick(adapterPosition)
            return false
        }
    }

    // MARK: Private

    private fun bindTextView(position: Int, holder: ViewHolder) {
        val thing = manager[position]
        holder.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                manager.update(holder.adapterPosition,
                        holder.editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        holder.button?.setOnClickListener { manager.removeAtIndex(holder.adapterPosition) }
        holder.editText?.setText(thing.data)
    }

    private fun bindLinkView(position: Int, holder: ViewHolder) {
        val thing = manager[position]
        holder.textView?.setOnClickListener {
            val p = holder.adapterPosition
            val innerThing = manager.get(p)
            listener.onItemClick(innerThing)
        }
        holder.textView?.setOnLongClickListener(holder)
        holder.textView?.text = thing.data
    }

    private fun bindImageView(position: Int, holder: ViewHolder) {
        val thing = manager[position]
        val uri = Uri.fromFile(File(thing.data))
        val imageStream: InputStream
        try {
            imageStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            holder.imageView?.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }
}
