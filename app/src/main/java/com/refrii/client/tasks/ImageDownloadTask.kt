package com.refrii.client.tasks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class ImageDownloadTask(private val callback: ImageDownloadTaskCallback) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg params: String): Bitmap? {
        var image: Bitmap? = null

        try {
            val imageUrl = URL(params[0])
            val imageIs: InputStream

            imageIs = imageUrl.openStream()
            image = BitmapFactory.decodeStream(imageIs)
        } catch (e: MalformedURLException) {
            Log.e(TAG, e.message)
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        }

        return image
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)

        callback.onPostExecuted(result)
    }

    companion object {
        private const val TAG = "ImageDownloadTask"
    }
}