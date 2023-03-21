package com.natife.streaming.ext

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import timber.log.Timber


fun SpeechRecognizer.doOnResult(block:(String)->Unit, onStart:()->Unit, onEnd:()->Unit,onError:(Int)->Unit){
    setRecognitionListener(object : RecognitionListener{
        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onBeginningOfSpeech() {
        onStart()
        }

        override fun onRmsChanged(rmsdB: Float) {

        }

        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {
            onEnd()
        }

        override fun onError(error: Int) {
          onError(error)
        }

        override fun onResults(results: Bundle?) {
            val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
            result?.let {
                block(it)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {

        }

        override fun onEvent(eventType: Int, params: Bundle?) {

        }

    })
}