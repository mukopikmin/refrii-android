package com.refrii.client

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by yusuke on 2017/09/24.
 */

internal class ImageDownloadTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg params: String): Bitmap? {
        val image: Bitmap
        try {
            val imageUrl = URL(params[0])
            val imageIs: InputStream
            imageIs = imageUrl.openStream()
            image = BitmapFactory.decodeStream(imageIs)
            return image
        } catch (e: MalformedURLException) {
            return null
        } catch (e: IOException) {
            return null
        }

    }

    override fun onPostExecute(result: Bitmap) {
        imageView.setImageBitmap(result)
    }
}