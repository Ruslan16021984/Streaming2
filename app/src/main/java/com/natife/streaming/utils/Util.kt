package com.natife.streaming.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.natife.streaming.data.Lexic
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.*


object Util {
    @Throws(Throwable::class)
    fun getThumbnailFromVideo(videoPath: String?, time: Long): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            bitmap = mediaMetadataRetriever.getFrameAtTime(time)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in getThumbnailFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }
}

class OneTimeScope(
    private val jobLimit: Int = 1,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatcher + job)
    private val jobs = mutableListOf<Job>()

    fun launch(block: suspend () -> Unit) {
        if (jobs.size < jobLimit) {
            jobs.add(scope.async { block() })
        } else {
            throw IllegalStateException("Job limit exceeded")
        }
    }

    suspend fun await() {
        job.children.forEach { it.join() }
        scope.cancel()
    }

}

fun List<Lexic>.findLexicText(context: Context?, id: Int) =
    this.find { it.id == context?.resources?.getInteger(id) }?.text

fun View.setMarginHorizontal(margin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(margin, params.topMargin, margin, params.bottomMargin)
    layoutParams = params
}

fun View.setSize(width: Int, height: Int) {
    layoutParams.width = width
    layoutParams.height = height
    requestLayout()
}

fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()
fun pxToDp(px: Float) = px / Resources.getSystem().displayMetrics.scaledDensity