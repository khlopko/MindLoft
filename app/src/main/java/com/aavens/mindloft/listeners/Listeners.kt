package com.aavens.mindloft.listeners

import com.aavens.mindloft.models.Thing

interface DialogFragmentActionsListener {
    fun onActionInteraction(index: Int?)
}

interface LinkEditDialogActionsListener {
    fun onDoneClick(url: String)
}

interface OnRoomClickListener {
    fun onItemClick(index: Int?)
    fun onItemLongClick(index: Int?)
}

interface OnThingClickListener {
    fun onItemClick(thing: Thing)
    fun onItemLongClick(index: Int)
}

interface ThingsTypeDialogListener {
    fun onThingTypeInteraction(index: Int?)
}
