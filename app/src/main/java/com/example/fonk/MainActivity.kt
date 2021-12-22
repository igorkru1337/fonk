package com.example.fonk

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myGLSurfaceView = GLSurfaceView(this)
        myGLSurfaceView.setEGLContextClientVersion(2)
        myGLSurfaceView.setRenderer(MyGL20Renderer())
        setContentView(myGLSurfaceView)
    }
}