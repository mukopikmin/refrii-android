package com.refrii.client.tasks

import android.graphics.Bitmap

interface ImageDownloadTaskCallback {
    fun onPostExecuted(result: Bitmap)
}