package com.example.vplayer.dataclass

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.vplayer.MainActivity
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

data class Video(
    val id: String,
    var title: String, val duration: Long = 0, val folderName: String, val size: String, var path: String, var artUri: Uri
) {
    var dateAdded: Date? = null
}

data class Folder(val id: String,val folderName: String)

@SuppressLint("Recycle", "Range", "SuspiciousIndentation")
fun getAllVideo(context: Context): ArrayList<Video>{
    val sortEditor = context.getSharedPreferences("Sorting", AppCompatActivity.MODE_PRIVATE)
    MainActivity.sortValue = sortEditor.getInt("sortValue",0)
    val tempList = ArrayList<Video>()
    val tempFolderList = ArrayList<String>()
    val projection = arrayOf(
        MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.BUCKET_ID)
    val cursor = context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
        MainActivity.sortList[MainActivity.sortValue])
    if(cursor != null)
        if(cursor.moveToNext())
            do {
                //checking null safety with ?: operator
                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))?:"Unknown"
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))?:"Unknown"
                val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))?:"Internal Storage"
                val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))?:"Unknown"
                val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))?:"0"
                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))?:"Unknown"
                val durationC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))?.toLong()?:0L
                val dateAddedInSeconds = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED))



                try {
                    val file = File(pathC)
                    val artUriC = Uri.fromFile(file)
                    val video = Video(
                        title = titleC, id = idC, folderName = folderC, duration = durationC, size = sizeC,
                        path = pathC, artUri = artUriC)
                    if(file.exists()) tempList.add(video)


                    if(!tempFolderList.contains(folderC) && !folderC.contains("Internal Storage")){
                        tempFolderList.add(folderC)
                        MainActivity.folderList.add(Folder(id = folderIdC, folderName = folderC))
                    }
                    val dateAddedInMillis = dateAddedInSeconds * 1000L
                    val dateAdded = Date(dateAddedInMillis)
                    video.dateAdded = dateAdded


                }catch (e:Exception){}


            }while (cursor.moveToNext())
    cursor?.close()
    return tempList
}
