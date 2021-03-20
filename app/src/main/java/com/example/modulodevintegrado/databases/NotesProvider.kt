package com.example.modulodevintegrado.databases

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns._ID
import androidx.annotation.RequiresApi
import com.example.modulodevintegrado.databases.NotesDatabaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.courses.applicationcontentprovider.provider"//define o endereço do provider... é meio que obrigatório
        const val NOTES = 1
        const val NOTES_BY_ID = 2
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")
    }

    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper
    override fun onCreate(): Boolean {//inicializa tudo no content provider
        //instancias do banco de dados
        //instancias das urls de pesquisa, etc etc etc
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)//sempre que for requisitado a url com.courses.applicationcontentprovider.provider/notes, retornar os esquemas la
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)// url/# quer dizer que tem mais coisas vindo

        if(context != null)
        {
            dbHelper = NotesDatabaseHelper(context as Context)
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {//deletar

        if(mUriMatcher.match(uri) == NOTES_BY_ID)
        {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val affectedLines: Int = db.delete(TABLE_NOTES, "$_ID", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)

            return affectedLines
        }
        else
        {
            throw UnsupportedSchemeException("Uri inválida para exclusão!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun getType(uri: Uri): String? {//validar dados interno + somente para requisições de arquivos
        throw UnsupportedSchemeException("Uri não implementada!")
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {//inserir

        if(mUriMatcher.match(uri) == NOTES)
        {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(TABLE_NOTES, null, values)

            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())

            db.close()

            context?.contentResolver?.notifyChange(uri, null)
            return insertUri
        }
        else
        {
            throw UnsupportedSchemeException("Uri inválida para inserção!")
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {//select
        return when{
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.readableDatabase
                val cursor = db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)

                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }

            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.readableDatabase
                val cursor = db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            else -> {
                throw UnsupportedSchemeException("Uri não implementada")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {//updateia

        if(mUriMatcher.match(uri) == NOTES_BY_ID)
        {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val affectedLines : Int = db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))

            db.close()

            context?.contentResolver?.notifyChange(uri, null)

            return affectedLines

        }
        else
        {
            throw UnsupportedSchemeException("Uri não implementada")
        }


    }


}
