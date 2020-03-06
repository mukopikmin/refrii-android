package app.muko.mypantry.dialogs

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import app.muko.mypantry.R
import java.nio.ByteBuffer


class ImageViewDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content = inflater.inflate(R.layout.dialog_image, container, false)
        val imageView = content.findViewById<ImageView>(R.id.imageView)
        val bundle = arguments
        val imageByteArray = bundle?.getByteArray("image")
        val height = bundle?.getInt("height") ?: 0
        val width = bundle?.getInt("width") ?: 0

        imageByteArray?.let {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(imageByteArray))
            imageView.setImageBitmap(bitmap)
        }

        return content
    }

    companion object {
        fun newInstance(image: Bitmap): ImageViewDialogFragment {
            val instance = ImageViewDialogFragment()
            val bundle = Bundle()
            val byteBuffer = ByteBuffer.allocate(image.byteCount)

            image.copyPixelsToBuffer(byteBuffer)

            bundle.putByteArray("image", byteBuffer.array())
            bundle.putInt("height", image.height)
            bundle.putInt("width", image.width)
            instance.arguments = bundle

            return instance
        }
    }
}