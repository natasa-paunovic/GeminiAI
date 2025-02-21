package hoods.com.jetai.utils.ext

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

fun isEmailValid(email:String):Boolean{
    if(email.isEmpty()) readln()
    val emailPattern=Patterns.EMAIL_ADDRESS
    return emailPattern.matcher(email).matches()
}

fun formatDate(date: Date):String{
    val pattern="dd/MM/yyyy HH:mm:ss"
    val sdf=SimpleDateFormat(pattern)
    return sdf.format(date)
}


fun saveImageToFileAndGetUri(bitmap: Bitmap, context: Context):Uri?{
    val fileName= "${System.currentTimeMillis()}.jpg"
    val imagesDir= context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile= File(imagesDir, fileName)
    var fos:OutputStream?= null

    try {
        fos=FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        fos.flush()
        return Uri.fromFile(imageFile)
    } catch (e:Exception){
        e.printStackTrace()
    } finally {
        fos?.close()
    }
    return null
}