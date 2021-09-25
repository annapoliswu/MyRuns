package com.zw.myruns

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Util {

    //checks an array of permissions and asks user for permission if one is missing
    fun checkPermissions(activity: Activity?, permissions: Array<String>){
        if (Build.VERSION.SDK_INT < 23) return
        var missingPermission : Boolean = false
        for(permission in permissions){
            if( ContextCompat.checkSelfPermission(activity!! , permission) != PackageManager.PERMISSION_GRANTED ){
                missingPermission = true
            }
        }
        if(missingPermission){
            ActivityCompat.requestPermissions( activity!!, permissions, 0)
        }
    }

    //from uri to bitmap with corrected rotation
    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var inputStream = context.contentResolver.openInputStream(imgUri)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        val matrix = Matrix()
        matrix.setRotate(90f)
        var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        inputStream?.close()
        return newBitmap
    }

    //writes an image at uri to app storage
    fun writeBitmap(context: Context, imgName: String, readUri: Uri, rotation : Float){
        val tempImgFile = File(context.getExternalFilesDir(null), imgName)
        val fOut = FileOutputStream(tempImgFile)
        val bitmap = getBitmap(context, readUri)
        val matrix = Matrix()
        matrix.setRotate(rotation)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).compress(
            Bitmap.CompressFormat.JPEG,
            70,
            fOut
        )
        fOut.flush()
        fOut.close()
    }


}