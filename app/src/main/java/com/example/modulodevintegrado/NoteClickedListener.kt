package com.example.modulodevintegrado

import android.database.Cursor

interface NoteClickedListener {

    fun noteClickedItem(cursor: Cursor)
    fun noteRemoveItem(cursor: Cursor?)

}