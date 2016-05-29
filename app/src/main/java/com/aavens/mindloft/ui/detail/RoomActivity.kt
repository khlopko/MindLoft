package com.aavens.mindloft.ui.detail

import android.app.Activity
import android.content.CursorLoader
import android.content.Intent
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.EditText

import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.DialogFragmentActionsListener
import com.aavens.mindloft.listeners.LinkEditDialogActionsListener
import com.aavens.mindloft.listeners.OnThingClickListener
import com.aavens.mindloft.listeners.ThingsTypeDialogListener
import com.aavens.mindloft.managers.RoomsManager
import com.aavens.mindloft.managers.ThingsManager
import com.aavens.mindloft.models.Room
import com.aavens.mindloft.models.Thing
import com.aavens.mindloft.ui.main.RoomActionsDialogFragment
import com.aavens.mindloft.ui.webpage.WebPageActivity

import java.util.Observable
import java.util.Observer

class RoomActivity:
        AppCompatActivity(),
        Observer,
        OnThingClickListener,
        ThingsTypeDialogListener,
        DialogFragmentActionsListener,
        LinkEditDialogActionsListener {

    private val RESULT_PHOTO = 101

    private var room: Room? = null
    private var editText: EditText? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<DetailRoomRecyclerViewAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = " "
        }
        initActivity()
        initRecyclerView()
        initFloatingButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            RESULT_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                val imageUri = intent.data
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cursorLoader = CursorLoader(this, imageUri, proj, null, null, null)
                val cursor = cursorLoader.loadInBackground()
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val filePath = cursor.getString(column_index)
                (adapter as DetailRoomRecyclerViewAdapter).addThingWithType(room!!.id, Thing.Type.IMAGE, filePath)
            }
        }
    }

    override fun onBackPressed() {
        val title = editText?.text.toString()
        room?.title = title
        RoomsManager.instance.update(room!!)
        if (title.isEmpty()) {
            editText?.error = "Field can't be empty!"
            return
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: OnThingClickListener

    override fun onItemClick(thing: Thing) {
        val intent = Intent(this, WebPageActivity::class.java)
        intent.putExtra(WebPageActivity.URL_EXTRA, thing.data)
        startActivity(intent)
    }

    override fun onItemLongClick(index: Int) {
        val fragment = RoomActionsDialogFragment(index, this)
        fragment.show(fragmentManager, "RoomActionsDialogFragment")
    }

    // MARK: DialogFragmentActionsListener

    override fun onActionInteraction(index: Int?) {
        (adapter as DetailRoomRecyclerViewAdapter).removeThingAtIndex(index!!)
    }

    // MARK: ThingsTypeDialogListener

    override fun onThingTypeInteraction(index: Int?) {
        if (index == null) {
            print("Illegal state!")
            return
        }
        val type = Thing.Type.values()[index]
        if (room == null) {
            print("Illegal state!")
            return
        }
        when (type) {
            Thing.Type.TEXT -> (adapter as DetailRoomRecyclerViewAdapter).addThingWithType(room!!.id, type, "")
            Thing.Type.LINK -> showLinkEditDialog()
            Thing.Type.IMAGE -> openImagePick()
        }
    }

    // MARK: LinkEditDialogActionsListener

    override fun onDoneClick(url: String) {
        val detailAdapter = adapter as DetailRoomRecyclerViewAdapter?
        detailAdapter?.addThingWithType(room!!.id, Thing.Type.LINK, url)
    }

    // MARK: Observer

    override fun update(observable: Observable, data: Any) {
        val actionInfo = data as ThingsManager.ActionInfo
        val position = actionInfo.position
        val action = actionInfo.action
        if (action == null) {
            print("Illegal state!")
            return
        }
        when (action) {
            ThingsManager.Action.ADD -> {
                if (position == -1) {
                    adapter?.notifyDataSetChanged()
                    return
                }
                adapter?.notifyItemInserted(position)
                recyclerView?.scrollToPosition(position)
            }
            ThingsManager.Action.UPDATE -> {
                adapter?.notifyItemChanged(position)
                recyclerView?.scrollToPosition(position)
            }
            ThingsManager.Action.REMOVE -> adapter?.notifyItemRemoved(position)
        }
    }

    // MARK: Private

    private fun initActivity() {
        editText = findViewById(R.id.title_edit_text) as EditText?
        val intent = intent
        val index = intent.getIntExtra(EXTRA_ROOM_INDEX, -1)
        val isNewRoom = index == -1
        if (!isNewRoom) {
            room = RoomsManager.instance[index]
            editText?.setText(room?.title)
        } else {
            createRoomIfNeeded("")
        }
        ThingsManager.instance.context = this
        ThingsManager.instance.roomId = room!!.id
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.things_list_view) as RecyclerView?
        adapter = DetailRoomRecyclerViewAdapter(this, this, this)
        recyclerView?.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.layoutManager = linearLayoutManager
    }

    private fun initFloatingButton() {
        val fab = findViewById(R.id.room_fab) as FloatingActionButton? ?: return
        fab.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val fragment = ThingTypeDialogFragment.newInstance(this)
        fragment.show(fragmentManager, "ThingTypeDialogFragment")
    }

    private fun createRoomIfNeeded(title: String) {
        if (room == null) {
            room = Room(title)
            RoomsManager.instance.append(room!!)
        }
    }

    private fun openImagePick() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_PHOTO)
    }

    private fun showLinkEditDialog() {
        val fragment = LinkEditDialogFragment.newInstance(this)
        fragment.show(fragmentManager, "LinkEditDialogFragment")
    }

    companion object {
        val EXTRA_ROOM_INDEX = "com.aavens.mindloft.ui.detail.EXATRA_ROOM_INDEX"
    }
}
