package com.example.fonk

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos

class MyGL20Renderer : GLSurfaceView.Renderer {

    companion object {
        private const val TIME: Long = 5000
    }

    private var sphere: Sphere? = null
    private var vmMatrix = FloatArray(16)
    private var mvpMatrix = FloatArray(16)
    private var pvmMatrix = FloatArray(16)
    private var invertMatrix = FloatArray(16)
    private var normalMatrix = FloatArray(16)
    private var rotationXMatrix = FloatArray(16)
    private var rotationYMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10, cfg: EGLConfig) {
        sphere = Sphere(30, 50)
        GLES20.glClearColor(215f, 230f, 45f, 1.0f)
        GLES20.glEnable(GL10.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GL10.GL_LEQUAL)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Matrix.frustumM(
            projectionMatrix,
            0,
            (-width).toFloat() / height,
            width.toFloat() / height,
            -1f,
            1f,
            1f,
            20f
        )
    }

    override fun onDrawFrame(unused: GL10) {
        val scaleMatrix = FloatArray(16)
        val finalMatrix = FloatArray(16)
        val temporaryMatrix = FloatArray(16)
        val angle =
            (cos(((SystemClock.uptimeMillis() % TIME).toFloat() / TIME * 2 * 3.1415926f).toDouble()) * 4f).toFloat()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(vmMatrix, 0, -1f, 0f, -1f, -1f, 0f, 0f, 25.0f, 5.0f, 25.0f)
        Matrix.multiplyMM(pvmMatrix, 0, projectionMatrix, 0, vmMatrix, 0)
        Matrix.setRotateM(rotationXMatrix, 0, 0f, 5.0f, 5.0f, 0f)
        Matrix.setRotateM(rotationYMatrix, 0, 0f, 5.0f, 5.0f, 0f)
        Matrix.multiplyMM(temporaryMatrix, 0, pvmMatrix, 0, rotationXMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, temporaryMatrix, 0, rotationYMatrix, 0)
        Matrix.multiplyMM(temporaryMatrix, 0, vmMatrix, 0, rotationXMatrix, 0)
        Matrix.multiplyMM(invertMatrix, 0, temporaryMatrix, 0, rotationYMatrix, 0)
        Matrix.invertM(temporaryMatrix, 0, invertMatrix, 0)
        Matrix.transposeM(normalMatrix, 0, temporaryMatrix, 0)
        Matrix.setLookAtM(vmMatrix, 0, angle, 0f, angle, 0f, 0f, 0f, 1f, 1f, 1f)
        Matrix.setIdentityM(scaleMatrix, 0)
        Matrix.scaleM(scaleMatrix, 0, 1.5f, 1.5f, 0.5f)
        Matrix.multiplyMM(temporaryMatrix, 0, mvpMatrix, 0, scaleMatrix, 0)
        Matrix.translateM(finalMatrix, 0, temporaryMatrix, 0, -0.6f, 0f, 5f)
        sphere?.draw(finalMatrix, normalMatrix, vmMatrix)
    }
}