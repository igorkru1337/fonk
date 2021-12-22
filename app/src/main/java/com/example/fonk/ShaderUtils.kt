package com.example.fonk

import android.opengl.GLES20

object ShaderUtils {

    private fun shaderInit(vertex_shader: Int, fragment_shader: Int): Int {
        val initId = GLES20.glCreateProgram()
        GLES20.glAttachShader(initId, vertex_shader)
        GLES20.glAttachShader(initId, fragment_shader)
        GLES20.glLinkProgram(initId)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(initId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(initId)
            return -2
        }
        return initId
    }

    private fun shaderHandler(type: Int, shader: String?): Int {
        val shaderId = GLES20.glCreateShader(type)
        if (shaderId == 0) {
            return -1
        }
        GLES20.glShaderSource(shaderId, shader)
        GLES20.glCompileShader(shaderId)
        val isShaderGood = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, isShaderGood, 0)
        if (isShaderGood[0] == 0) {
            GLES20.glDeleteShader(shaderId)
            return -2
        }
        return shaderId
    }

    fun createShaders(): Int {
        val vertexShader = "uniform mat4 uMVPMatrix, uMVMatrix, uNormalMat;" +
                "attribute vec4 vPosition;" +
                "attribute vec4 vColor;" +
                "attribute vec3 vNormal;" +
                "varying vec4 varyingColor;" +
                "varying vec3 varyingNormal;" +
                "varying vec3 varyingPos;" +
                "void main()" +
                "{" +
                "varyingColor = vColor;" +
                "varyingNormal= vec3(uNormalMat * vec4(vNormal, 0.0));" +
                "varyingPos = vec3(uMVMatrix * vPosition);" +
                "gl_Position = uMVPMatrix * vPosition;" +
                "}"
        val fragmentShader = "precision mediump float;" +
                "varying vec4 varyingColor; varying vec3 varyingNormal;" +
                "varying vec3 varyingPos;" +
                "uniform vec3 lightDir;" +
                "void main()" +
                "{" +
                "float Ns = 4.0;" +
                "float kd = 5.9, ks = 5.0;" +
                "vec4 light = vec4(0.0, 0.0, 0.0, 0.0);" +
                "vec4 lightS = vec4(0.0, 1.0, 1.0, 0.0);" +
                "vec3 Nn = normalize(varyingNormal);" +
                "vec3 Ln = normalize(lightDir);" +
                "vec4 diffuse = kd * light * max(dot(Nn, Ln), 0.0);" +
                "vec3 Ref = reflect(Nn, Ln);" +
                "float spec = pow(max(dot(Ref, normalize(varyingPos)), 0.0), Ns);" +
                "vec4 specular = lightS * ks * spec;" +
                "gl_FragColor = varyingColor * diffuse + specular;" +
                "}"
        val vertexShaderId = shaderHandler(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = shaderHandler(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        return shaderInit(vertexShaderId, fragmentShaderId)
    }
}