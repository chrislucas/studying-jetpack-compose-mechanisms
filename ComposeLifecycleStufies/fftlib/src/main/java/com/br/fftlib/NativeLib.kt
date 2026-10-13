package com.br.fftlib

class NativeLib {

    /**
     * A native method that is implemented by the 'fftlib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'fftlib' library on application startup.
        init {
            System.loadLibrary("fftlib")
        }
    }
}