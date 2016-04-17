package com.aavens.mindloft.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.OnRoomClickListener;
import com.aavens.mindloft.managers.RoomsManager;
import com.aavens.mindloft.models.Room;

public class RoomsRecyclerViewAdapter extends RecyclerView.Adapter<RoomsRecyclerViewAdapter.ViewHolder> {

    private OnRoomClickListener listener;

    public RoomsRecyclerViewAdapter(OnRoomClickListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Room room = RoomsManager.getInstance().get(position);
        String text = room.getTitle();
        holder.title.setText(text);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.mView.setOnLongClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return RoomsManager.getInstance().getCount();
    }

    // MARK: ViewHolder Class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final View mView;
        public final TextView title;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.info_text);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onItemLongClick(getAdapterPosition());
            return false;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
