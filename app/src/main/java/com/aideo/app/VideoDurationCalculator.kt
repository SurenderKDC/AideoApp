package com.aideo.app
import android.media.MediaMetadataRetriever
import android.os.AsyncTask


class VideoDurationCalculator(private val listener: OnVideoDurationListener) : AsyncTask<String, Void, Long?>() {

    interface OnVideoDurationListener {
        fun onDurationCalculated(duration: Long?)
        fun onError(error: Exception)
    }

    override fun doInBackground(vararg params: String?): Long? {
        val videoUrl = params[0]
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap<String, String>())
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)
        listener.onDurationCalculated(result)
    }
}