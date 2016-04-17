package com.aavens.mindloft.ui.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.OnThingClickListener;
import com.aavens.mindloft.managers.ThingsManager;
import com.aavens.mindloft.models.Thing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Observer;

public class DetailRoomRecyclerViewAdapter
        extends RecyclerView.Adapter<DetailRoomRecyclerViewAdapter.ViewHolder> {

    private OnThingClickListener listener;
    private Context context;
    private ThingsManager manager = ThingsManager.getInstance();

    public DetailRoomRecyclerViewAdapter(Context context,
                                         OnThingClickListener listener,
                                         Observer observer) {
        this.context = context;
        this.listener = listener;
        manager.addObserver(observer);
    }

    public void addThingWithType(long roomId, Thing.Type type, String value) {
        Thing thing = new Thing(type, value, roomId);
        manager.append(thing);
    }

    public void removeThingAtIndex(int index) {
        manager.removeAtIndex(index);
    }

    // MARK: Override

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Thing.Type type = Thing.Type.values()[viewType];
        int id = 0;
        switch (type) {
            case TEXT:
                id = R.layout.text_item;
                break;
            case LINK:
                id = R.layout.link_item;
                break;
            case IMAGE:
                id = R.layout.image_item;
                break;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(id, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setOnLongClickListener(holder);
        Thing.Type type = manager.get(position).getType();
        switch (type) {
            case TEXT:
                bindTextView(position, holder);
                break;
            case LINK:
                bindLinkView(position, holder);
                break;
            case IMAGE:
                bindImageView(position, holder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return manager.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return manager.getCount();
    }

    // MARK: ViewHolder Class

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final View mView;
        public final EditText editText;
        public final ImageButton button;
        public final TextView textView;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            editText = (EditText) view.findViewById(R.id.thing_text);
            textView = (TextView) view.findViewById(R.id.link_text);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            button = (ImageButton) view.findViewById(R.id.remove_text_item_button);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onItemLongClick(getAdapterPosition());
            return false;
        }
    }

    // MARK: Private

    private void bindTextView(final int position, final ViewHolder holder) {
        Thing thing = manager.get(position);
        holder.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    manager.update(holder.getAdapterPosition(),
                            holder.editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.removeAtIndex(holder.getAdapterPosition());
            }
        });
        holder.editText.setText(thing.getData());
    }

    private void bindLinkView(final int position, final ViewHolder holder) {
        Thing thing = manager.get(position);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = holder.getAdapterPosition();
                Thing innerThing = manager.get(p);
                listener.onItemClick(innerThing);
            }
        });
        holder.textView.setOnLongClickListener(holder);
        holder.textView.setText(thing.getData());
    }

    private void bindImageView(final int position, final ViewHolder holder) {
        Thing thing = manager.get(position);
        Uri uri = Uri.fromFile(new File(thing.getData()));
        final InputStream imageStream;
        try {
            imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            holder.imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
