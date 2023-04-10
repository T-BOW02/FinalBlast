import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

abstract class GameObject(protected val context: Context) {
    open var x: Float = 0f
    open var y: Float = 0f
    open var width: Int = 0
    open var height: Int = 0
    open lateinit var bitmap: Bitmap

    protected fun setBitmap(resourceId: Int, reqWidth: Int, reqHeight: Int) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeResource(context.resources, resourceId, options)

        options.apply {
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }

        bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        width = bitmap.width
        height = bitmap.height
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (width, height) = options.run { outWidth to outHeight }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    open fun update() {
        // Override this method in subclasses to implement update logic.
    }

    open fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
}