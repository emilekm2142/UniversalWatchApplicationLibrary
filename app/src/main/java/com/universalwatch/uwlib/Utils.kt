package com.universalwatch.uwlib

import android.content.Context
import android.content.Intent

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

val DEBUG_DEFAULT_COMMUNICATION_APPLICATION_PACKAGE = "io.universalwatch.universalwatchapplication"

/**
 * Created by emile on 19.02.2018.
 */
object WatchUtils {
    fun BitmapToUri(context: Context, image:Bitmap, imageName:String):Uri{
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.getAbsolutePath() + "/.temp/")
        tempDir.mkdir()
        val tempFile = File.createTempFile(imageName, ".jpg", tempDir)
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return Uri.fromFile(tempFile)
    }
    fun copyFromAssets(context:Context, name:String):Uri?{

        val bufferSize = 1024
        val assetManager = context.assets
        val assetFile = assetManager.open(name);

        assetFile?.let {

            val outputStream = FileOutputStream(File(context.filesDir, context.filesDir.toString()+"/"+name))

            try {
                assetFile.copyTo(outputStream, bufferSize)
            } finally {
                assetFile.close()
                outputStream.flush()
                outputStream.close()
                return Uri.parse(context.filesDir.toString()+"/"+name)
            }
        }
        return null;

    }
    fun isFileInAssets(context: Context, name:String):Boolean{
        val assetManager = context.assets
        try {
            val assetFile = assetManager.open(name);
            return true;
        }
        catch (e:Exception){
            //file not found
            return false
        }
    }
    suspend fun getPublicUri(context:Context, name:String):Uri{
        if (name.startsWith("http")){

        }
        if (isFileInAssets(context,name)){
            val fileUri = copyFromAssets(context,name);
            return fileUri!!
        }else{
            val f  = File(name)
            if(f.exists()){
                return Uri.parse(name)
            }
        }
        val uri = Uri.parse(context.filesDir.toString()+"/"+name)
        val f  = File(name)
        if(f.exists()){
            return uri
        }
        else{
            throw FileNotFoundException("Could not find a proper file in assets, by path and in filesDir. Does it really exist? File:" + name)
        }
    }
    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            return null
        }

    }
    fun unescapeString(string:String):String{
        return JavaWatchUtils.unescapeJavaString(string)
    }

    fun sendDebugString(s:String,context: Context, packageName:String = DEBUG_DEFAULT_COMMUNICATION_APPLICATION_PACKAGE){
        val i = Intent("BT_DEBUG")
        i.setPackage(packageName)
        i.putExtra("message",s)
        context.sendBroadcast(i)
    }
    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start
    fun convertUriToImage(context: Context, uri: Uri):Bitmap{
        Log.d("co jest kurwa", "kurwa")
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
        }
        catch(e:java.io.FileNotFoundException){
            val stran = context.assets.open(uri.toString().replace("file:///","").replace("assets/",""))
            val decoded =  BitmapFactory.decodeStream(stran)
            return decoded
        }
    }


    fun saveBitmap(context: Context, mBitmap:Bitmap):Uri{
        val f3 = File(Environment.getExternalStorageDirectory().toString() + "/temporal/")
        if (!f3.exists())
            f3.mkdirs()
        var outStream: OutputStream? = null
        val path =context.filesDir.toString() + "/" + "img" +( (0..1000).random() * (0..1000).random()).toString()+".jpg"

        try {
            outStream = FileOutputStream(path)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
            outStream!!.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("path", path)
        return Uri.parse(path)
    }

    fun encodeToBase64(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): String {
        val byteArrayOS = ByteArrayOutputStream()
        image.compress(compressFormat, quality, byteArrayOS)
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT).replace("\n", "")
    }

    fun decodeBase64(input: String): Bitmap {

        val pureBase64Encoded = input.substring(input.indexOf(",")  + 1);
        val decodedBytes = Base64.decode(pureBase64Encoded, 0)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

}