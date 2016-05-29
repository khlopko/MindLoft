package com.aavens.mindloft.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.DialogFragmentActionsListener
import com.aavens.mindloft.listeners.OnRoomClickListener
import com.aavens.mindloft.managers.RoomsManager
import com.aavens.mindloft.ui.detail.RoomActivity
import java.util.*

class MainActivity: AppCompatActivity(),
        Observer,
        OnRoomClickListener,
        DialogFragmentActionsListener {

    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<RoomRecycleViewAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RoomsManager.instance.context = this
        RoomsManager.instance.addObserver(this)
        initToolbar()
        initFloatingButton()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // MARK: OnRoomClickListener

    override fun onItemClick(index: Int?) {
        navigateToRoomActivity(index)
    }

    override fun onItemLongClick(index: Int?) {
        if (index != null) {
            val fragment = RoomActionsDialogFragment(index.toInt(), this)
            fragment.show(fragmentManager, "RoomActionsDialogFragment")
        }
    }

    // MARK: Observer

    override fun update(observable: Observable, data: Any) {
        val actionInfo = data as RoomsManager.ActionInfo
        val position = actionInfo.position
        when (actionInfo.action) {
            RoomsManager.Action.ADD -> {
                adapter?.notifyItemInserted(position)
                recyclerView?.scrollToPosition(position)
            }
            RoomsManager.Action.UPDATE -> adapter?.notifyItemChanged(position)
            RoomsManager.Action.REMOVE -> adapter?.notifyItemRemoved(position)
        }
    }

    // MARK: DialogFragmentActionsListener

    override fun onActionInteraction(index: Int?) {
        if (index != null) {
            val room = RoomsManager.instance[index]
            RoomsManager.instance.delete(room)
        }
    }

    // MARK: Private

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
    }

    private fun initFloatingButton() {
        val fab = findViewById(R.id.fab) as FloatingActionButton? ?: return
        fab.setOnClickListener { navigateToRoomActivity(null) }
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rooms_list_view) as RecyclerView?
        adapter = RoomRecycleViewAdapter(this)
        recyclerView?.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.layoutManager = linearLayoutManager
    }

    private fun navigateToRoomActivity(index: Int?) {
        val intent = Intent(this, RoomActivity::class.java)
        intent.putExtra(RoomActivity.EXTRA_ROOM_INDEX, index)
        startActivity(intent)
    }
}
