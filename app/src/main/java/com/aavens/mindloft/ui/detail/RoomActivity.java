package com.aavens.mindloft.ui.detail;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.DialogFragmentActionsListener;
import com.aavens.mindloft.listeners.LinkEditDialogActionsListener;
import com.aavens.mindloft.listeners.OnThingClickListener;
import com.aavens.mindloft.listeners.ThingsTypeDialogListener;
import com.aavens.mindloft.managers.RoomsManager;
import com.aavens.mindloft.managers.ThingsManager;
import com.aavens.mindloft.models.Room;
import com.aavens.mindloft.models.Thing;
import com.aavens.mindloft.ui.main.RoomActionsDialogFragment;
import com.aavens.mindloft.ui.webpage.WebPageActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class RoomActivity
        extends AppCompatActivity
        implements
        Observer,
        OnThingClickListener,
        ThingsTypeDialogListener,
        DialogFragmentActionsListener,
        LinkEditDialogActionsListener {

    public static final String EXTRA_ROOM_INDEX = "com.aavens.mindloft.ui.detail.EXATRA_ROOM_INDEX";

    private final int RESULT_PHOTO = 101;

    private Room room;
    private EditText editText;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(" ");
        }
        initActivity();
        initRecyclerView();
        initFloatingButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case RESULT_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = intent.getData();
                    String[] proj = { MediaStore.Images.Media.DATA };
                    CursorLoader cursorLoader = new CursorLoader(this, imageUri, proj, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String filePath = cursor.getString(column_index);
                    ((DetailRoomRecyclerViewAdapter) adapter)
                            .addThingWithType(room.getId(), Thing.Type.IMAGE, filePath);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        String title = editText.getText().toString();
        room.setTitle(title);
        RoomsManager.getInstance().update(room);
        if (title.isEmpty()) {
            editText.setError("Field can't be empty!");
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // MARK: OnThingClickListener
    
    @Override
    public void onItemClick(Thing thing) {
        Intent intent = new Intent(this, WebPageActivity.class);
        intent.putExtra(WebPageActivity.URL_EXTRA, thing.getData());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int index) {
        RoomActionsDialogFragment fragment = RoomActionsDialogFragment.newInstance(index, this);
        fragment.show(getFragmentManager(), "RoomActionsDialogFragment");
    }

    // MARK: DialogFragmentActionsListener

    @Override
    public void onActionInteraction(Integer index) {
        ((DetailRoomRecyclerViewAdapter) adapter).removeThingAtIndex(index);
    }

    // MARK: ThingsTypeDialogListener

    @Override
    public void onThingTypeInteraction(Integer index) {
        Thing.Type type = Thing.Type.values()[index];
        switch (type) {
            case TEXT:
                ((DetailRoomRecyclerViewAdapter) adapter).addThingWithType(room.getId(), type, null);
                break;
            case LINK:
                showLinkEditDialog();
                break;
            case IMAGE:
                openImagePick();
                break;
        }
    }

    // MARK: LinkEditDialogActionsListener

    @Override
    public void onDoneClick(String url) {
        DetailRoomRecyclerViewAdapter detailAdapter = (DetailRoomRecyclerViewAdapter) adapter;
        detailAdapter.addThingWithType(room.getId(), Thing.Type.LINK, url);
    }

    // MARK: Observer

    @Override
    public void update(Observable observable, Object data) {
        ThingsManager.ActionInfo actionInfo = (ThingsManager.ActionInfo) data;
        int position = actionInfo.getPosition();
        switch (actionInfo.getAction()) {
            case ADD:
                if (position == -1) {
                    adapter.notifyDataSetChanged();
                    return;
                }
                adapter.notifyItemInserted(position);
                recyclerView.scrollToPosition(position);
                break;
            case UPDATE:
                adapter.notifyItemChanged(position);
                recyclerView.scrollToPosition(position);
                break;
            case REMOVE:
                adapter.notifyItemRemoved(position);
                break;
        }
    }

    // MARK: Private

    private void initActivity() {
        editText = (EditText) findViewById(R.id.title_edit_text);
        Intent intent = getIntent();
        Integer index = intent.getIntExtra(EXTRA_ROOM_INDEX, -1);
        Boolean isNewRoom = index == -1;
        if (!isNewRoom) {
            room = RoomsManager.getInstance().get(index);
            editText.setText(room.getTitle());
        } else {
            createRoomIfNeeded("");
        }
        ThingsManager.initialize(this, room.getId());
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.things_list_view);
        adapter = new DetailRoomRecyclerViewAdapter(this, this, this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initFloatingButton() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.room_fab);
        if (fab == null) {
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        ThingTypeDialogFragment fragment = ThingTypeDialogFragment.newInstance(this);
        fragment.show(getFragmentManager(), "ThingTypeDialogFragment");
    }

    private void createRoomIfNeeded(String title) {
        if (room == null) {
            room = new Room(title);
            RoomsManager.getInstance().append(room);
        }
    }

    private void openImagePick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_PHOTO);
    }

    private void showLinkEditDialog() {
        LinkEditDialogFragment fragment = LinkEditDialogFragment.newInstance(this);
        fragment.show(getFragmentManager(), "LinkEditDialogFragment");
    }
}
