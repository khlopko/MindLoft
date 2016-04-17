package com.aavens.mindloft.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.DialogFragmentActionsListener;
import com.aavens.mindloft.listeners.OnRoomClickListener;
import com.aavens.mindloft.managers.RoomsManager;
import com.aavens.mindloft.models.Room;
import com.aavens.mindloft.ui.detail.RoomActivity;
import java.util.Observable;
import java.util.Observer;

public class MainActivity
        extends AppCompatActivity
        implements
        OnRoomClickListener,
        Observer,
        DialogFragmentActionsListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RoomsManager.getInstance().setContext(this);
        RoomsManager.getInstance().addObserver(this);
        initToolbar();
        initFloatingButton();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // MARK: OnRoomClickListener

    @Override
    public void onItemClick(Integer index) {
        navigateToRoomActivity(index);
    }

    @Override
    public void onItemLongClick(Integer index) {
        RoomActionsDialogFragment fragment = RoomActionsDialogFragment.newInstance(index, this);
        fragment.show(getFragmentManager(), "RoomActionsDialogFragment");
    }

    // MARK: Observer

    @Override
    public void update(Observable observable, Object data) {
        RoomsManager.ActionInfo actionInfo = (RoomsManager.ActionInfo) data;
        int position = actionInfo.getPosition();
        switch (actionInfo.getAction()) {
            case ADD:
                adapter.notifyItemInserted(position);
                recyclerView.scrollToPosition(position);
                break;
            case UPDATE:
                adapter.notifyItemChanged(position);
                break;
            case REMOVE:
                adapter.notifyItemRemoved(position);
                break;
        }
    }

    // MARK: DialogFragmentActionsListener

    public void onActionInteraction(Integer index) {
        Room room = RoomsManager.getInstance().get(index);
        RoomsManager.getInstance().delete(room);
    }

    // MARK: Private

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFloatingButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) {
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRoomActivity(null);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rooms_list_view);
        adapter = new RoomsRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void navigateToRoomActivity(Integer index) {
        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra(RoomActivity.EXTRA_ROOM_INDEX, index);
        startActivity(intent);
    }
}
