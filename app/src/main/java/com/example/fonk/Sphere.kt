package com.example.fonk

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class Sphere(lats: Int, longs: Int) {

    companion object {
        const val COORDS_PER_VERTEX = 3
        lateinit var colors: FloatArray
    }

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val mProgram: Int
    private lateinit var vertices: FloatArray
    private lateinit var normals: FloatArray
    private var vertexCount = 0
    private var lightDir = floatArrayOf(1.0f, 1.0f, 1.0f)

    init {
        createSphere(lats, longs)
        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        val bb2 = ByteBuffer.allocateDirect(colors.size * 4)
        bb2.order(ByteOrder.nativeOrder())
        colorBuffer = bb2.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)
        val bb3 = ByteBuffer.allocateDirect(normals.size * 4)
        bb3.order(ByteOrder.nativeOrder())
        normalBuffer = bb3.asFloatBuffer()
        normalBuffer.put(normals)
        normalBuffer.position(0)
        val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        val drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
        mProgram = ShaderUtils.createShaders()
    }

    private fun createSphere(lats: Int, longs: Int) {
        vertices = FloatArray(lats * longs * 6 * 3)
        normals = FloatArray(lats * longs * 6 * 3)
        colors = FloatArray(lats * longs * 6 * 3)
        var triIndex = 0
        vertexCount = vertices.size / COORDS_PER_VERTEX
        for (i in 0 until lats) {
            val lat0 = Math.PI * (-0.5 + i.toDouble() / lats)
            val z0 = sin(lat0)
            val zr0 = cos(lat0)
            val lat1 = Math.PI * (-0.5 + (i + 1).toDouble() / lats)
            val z1 = sin(lat1)
            val zr1 = cos(lat1)
            for (j in 0 until longs) {
                var lng = 2 * Math.PI * (j - 1).toDouble() / longs
                val x = cos(lng)
                val y = sin(lng)
                lng = 2 * Math.PI * j.toDouble() / longs
                val x1 = cos(lng)
                val y1 = sin(lng)
                vertices[triIndex * 9] = (x * zr0).toFloat()
                vertices[triIndex * 9 + 1] = (y * zr0).toFloat()
                vertices[triIndex * 9 + 2] = z0.toFloat()
                vertices[triIndex * 9 + 3] = (x * zr1).toFloat()
                vertices[triIndex * 9 + 4] = (y * zr1).toFloat()
                vertices[triIndex * 9 + 5] = z1.toFloat()
                vertices[triIndex * 9 + 6] = (x1 * zr0).toFloat()
                vertices[triIndex * 9 + 7] = (y1 * zr0).toFloat()
                vertices[triIndex * 9 + 8] = z0.toFloat()
                triIndex++
                vertices[triIndex * 9] = (x1 * zr0).toFloat()
                vertices[triIndex * 9 + 1] = (y1 * zr0).toFloat()
                vertices[triIndex * 9 + 2] = z0.toFloat()
                vertices[triIndex * 9 + 3] = (x * zr1).toFloat()
                vertices[triIndex * 9 + 4] = (y * zr1).toFloat()
                vertices[triIndex * 9 + 5] = z1.toFloat()
                vertices[triIndex * 9 + 6] = (x1 * zr1).toFloat()
                vertices[triIndex * 9 + 7] = (y1 * zr1).toFloat()
                vertices[triIndex * 9 + 8] = z1.toFloat()
                for (kk in -9..8) {
                    normals[triIndex * 9 + kk] = vertices[triIndex * 9 + kk]
                    colors[triIndex * 9 + kk] = 0f
                }
                triIndex++
            }
        }
    }

    fun draw(mvpMatrix: FloatArray?, normalMat: FloatArray?, mvMat: FloatArray?) {
        GLES20.glUseProgram(mProgram)
        val mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        val vertexStride = COORDS_PER_VERTEX * 4
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        val light = GLES20.glGetUniformLocation(mProgram, "lightDir")
        GLES20.glUniform3fv(light, 1, lightDir, 0)
        val mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
        GLES20.glEnableVertexAttribArray(mColorHandle)
        GLES20.glVertexAttribPointer(
            mColorHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )
        val mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal")
        GLES20.glEnableVertexAttribArray(mNormalHandle)
        GLES20.glVertexAttribPointer(
            mNormalHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            normalBuffer
        )
        val mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        val mNormalMatHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMat")
        val mVMatHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(mNormalMatHandle, 1, false, normalMat, 0)
        GLES20.glUniformMatrix4fv(mVMatHandle, 1, false, mvMat, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mColorHandle)
        GLES20.glDisableVertexAttribArray(mNormalHandle)
    }
}