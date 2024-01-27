package site.feiyuliuxing.buildpoints.utils

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import java.nio.FloatBuffer

object GLUtil {
    private const val TAG = "GLUtil"

    fun compileShaders(
        context: Context,
        vertexShaderAssetsFile: String,
        fragmentShaderAssetsFile: String
    ): Int {
        val vertexShaderSource = context.assets.open(vertexShaderAssetsFile).use {
            String(it.readBytes())
        }
        val fragmentShaderSource = context.assets.open(fragmentShaderAssetsFile).use {
            String(it.readBytes())
        }
        return compileShaders(vertexShaderSource, fragmentShaderSource)
    }

    private fun compileShaders(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val status = intArrayOf(0)

        val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
        GLES30.glShaderSource(vertexShader, vertexShaderSource)
        GLES30.glCompileShader(vertexShader)
        GLES30.glGetShaderiv(vertexShader, GLES30.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "顶点着色器编译失败：" + GLES30.glGetShaderInfoLog(vertexShader))
        }

        val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
        GLES30.glShaderSource(fragmentShader, fragmentShaderSource)
        GLES30.glCompileShader(fragmentShader)
        GLES30.glGetShaderiv(fragmentShader, GLES30.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "片段着色器编译失败：" + GLES30.glGetShaderInfoLog(fragmentShader))
        }

        val program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "链接源程序失败：" + GLES30.glGetProgramInfoLog(program))
        }

        // Delete the shaders as the program has them now
        GLES30.glDeleteShader(vertexShader)
        GLES30.glDeleteShader(fragmentShader)

        return program
    }

    /**
     * 顶点数据加载到缓冲区
     */
    fun setVertexBufferData(positionHandle: Int, vertexBuffer: FloatBuffer, vbo: Int) {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexBuffer.capacity() * 4,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        GLES30.glVertexAttribPointer(positionHandle, 2, GLES30.GL_FLOAT, false, 0, 0)
    }


}