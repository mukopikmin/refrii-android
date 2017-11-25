package com.refrii.client.tasks

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

@SuppressLint("StaticFieldLeak")
class ImageDownloadTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg params: String): Bitmap? {
        val image: Bitmap

        return try {
            val imageUrl = URL(params[0])
            val imageIs: InputStream

            imageIs = imageUrl.openStream()
            image = BitmapFactory.decodeStream(imageIs)
            image
        } catch (e: MalformedURLException) {
            null
        } catch (e: IOException) {
            null
        }

    }

    override fun onPostExecute(result: Bitmap) {
        imageView.setImageBitmap(result)
    }
}