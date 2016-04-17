package com.aavens.mindloft.listeners;

import com.aavens.mindloft.models.Thing;

public interface OnThingClickListener {
    void onItemClick(Thing thing);
    void onItemLongClick(int index);
}
