package site.feiyuliuxing.buildpoints.glview

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import site.feiyuliuxing.buildpoints.BuildPoints
import site.feiyuliuxing.buildpoints.utils.ColorTemplate
import site.feiyuliuxing.buildpoints.utils.GLUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.atan2

private const val TAG = "MyGLRenderer"

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var muMVPMatrixHandle = 0
    private var programHandle = 0
    private var positionHandle = 0
    private var colorHandle = 0
    private var isTextureHandle = 0
    private var textureCoordHandle = 0
    private var translateMatrixHandle = 0
    private var isTranslateHandle = 0

    //(相机)视图矩阵
    private val vMatrix = FloatArray(16)
    private val vPMatrix = FloatArray(16)

    //投影矩阵
    private val projMatrix = FloatArray(16)

    // VAO: Vertex Array Object
    // VBO: Vertex Buffer Object
    private val vaoNum = 1
    private val vboNum = 2

    private val vao = IntArray(vaoNum)
    private val vbo = IntArray(vboNum)

    //画轨迹线使用的顶点
    private val vertexBuffers = mutableListOf<FloatBuffer>()

    //画箭头使用点顶点，使用3个点，6个float，即24字节
    private val arrowVertex = floatArrayOf(
        -0.03f, 0.02f,
        0.0f, 0.0f,
        -0.03f, -0.02f
    )
    private val arrowVertexBuffer = ByteBuffer.allocateDirect(24)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(arrowVertex)
        .apply { position(0) }

    private val arrowMatrix = FloatArray(16)
    private val arrowRotateMatrix = FloatArray(16)
    private val arrowTranslateMatrix = FloatArray(16)

    private val glColors: List<FloatArray> = ColorTemplate.MATERIAL_COLORS.map { color ->
        floatArrayOf(
            color.shr(16).and(0xff) / 255f,
            color.shr(8).and(0xff) / 255f,
            color.and(0xff) / 255f,
        )
    }

    fun addPoints(points: FloatArray) {
        val buffer = ByteBuffer.allocateDirect(points.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(points)
            .apply { position(0) }
        vertexBuffers.add(buffer)
    }

    private fun drawFrame() {
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        //下面两行代码，防止图片的透明部分被显示成黑色
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glEnableVertexAttribArray(textureCoordHandle)//启用纹理坐标属性

        //画几何图形
        GLES30.glUniform1i(isTextureHandle, 0)
        GLES30.glUniform1i(isTranslateHandle, 0)

        for (i in vertexBuffers.indices) {
            val colorArr = glColors[i % glColors.size]
            drawLinesAndArrows(vertexBuffers[i], colorArr[0], colorArr[1], colorArr[2])
        }

        //画bitmap
//        GLES30.glUniform1i(isTextureHandle, 1)

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(textureCoordHandle)
    }

    private fun drawLinesAndArrows(dataBuffer: FloatBuffer, r: Float, g: Float, b: Float) {
        GLES30.glUniform1i(isTranslateHandle, 0)
        //顶点数据加载到缓冲区
        GLUtil.setVertexBufferData(positionHandle, dataBuffer, vbo[0])
        //设置颜色
        GLES30.glUniform4f(colorHandle, r, g, b, 1f)
        GLES30.glLineWidth(4f)
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, dataBuffer.capacity() / 2)

        // todo 画线上表示方向的箭头
        GLES30.glUniform1i(isTranslateHandle, 1)//启用偏移矩阵
        GLES30.glLineWidth(3f)
        for (i in 0..dataBuffer.capacity() - 4 step 2) {
            val x1 = dataBuffer.get(i)
            val y1 = dataBuffer.get(i + 1)
            val x2 = dataBuffer.get(i + 2)
            val y2 = dataBuffer.get(i + 3)

            if (x1 == x2 && y1 == y2) continue

            //设置箭头的<旋转x偏移>矩阵
            rotateAndTranslateArrow(x1, y1, x2, y2)

            GLUtil.setVertexBufferData(positionHandle, arrowVertexBuffer, vbo[0])
            GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 3)
        }
    }

    private fun rotateAndTranslateArrow(x1: Float, y1: Float, x2: Float, y2: Float) {
        val radians = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        val degree = Math.toDegrees(radians).toFloat()

        //https://developer.android.com/reference/android/opengl/Matrix
        Matrix.setIdentityM(arrowRotateMatrix, 0)
        Matrix.rotateM(arrowRotateMatrix, 0, degree, 0f, 0f, 1f)
        Matrix.setIdentityM(arrowTranslateMatrix, 0)
        Matrix.translateM(arrowTranslateMatrix, 0, (x1 + x2) / 2f, (y1 + y2) / 2f, 0f)
        Matrix.setIdentityM(arrowMatrix, 0)
        Matrix.multiplyMM(arrowMatrix, 0, arrowTranslateMatrix, 0, arrowRotateMatrix, 0)
        GLES30.glUniformMatrix4fv(translateMatrixHandle, 1, false, arrowMatrix, 0)

        /*var arr = BuildPoints.rotate(arrowVertex, 0f, 0f, degree)
        arr = BuildPoints.shift(arr, (x1 + x2) / 2f, (y1 + y2) / 2f)//在线段中间画箭头
//            arr = BuildPoints.shift(arr, x2, y2)//在线段尾部画箭头
        arrowVertexBuffer.apply {
            position(0)
            put(arr)
            position(0)
        }*/
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.i(TAG, "onSurfaceCreated()")
        programHandle = GLUtil.compileShaders(
            context, "vertexShader.glsl",
            "fragmentShader.glsl"
        )
        GLES30.glUseProgram(programHandle)
        muMVPMatrixHandle = GLES30.glGetUniformLocation(programHandle, "uMVPMatrix")
        positionHandle = GLES30.glGetAttribLocation(programHandle, "vPosition")
        textureCoordHandle = GLES30.glGetAttribLocation(programHandle, "aTextureCoord")
        colorHandle = GLES30.glGetUniformLocation(programHandle, "vColor")
        isTextureHandle = GLES30.glGetUniformLocation(programHandle, "isTexture")
        translateMatrixHandle = GLES30.glGetUniformLocation(programHandle, "translateMatrix")
        isTranslateHandle = GLES30.glGetUniformLocation(programHandle, "isTranslate")

        GLES30.glGenVertexArrays(vaoNum, vao, 0)
        GLES30.glBindVertexArray(vao[0])
        GLES30.glGenBuffers(vboNum, vbo, 0)

        // 第5个参数 eyeZ 为正值，视点在屏幕前方，负值则在屏幕后方，其符号会影响X轴方向
        // 其绝对值影响绘制元素的大小，绝对值越大，视点离屏幕越远，物体越小
        // 倒数第2个参数 upY 的符号是影响Y轴坐标是否反转的，如果发现图片上下或者左右翻转了，就要调整这俩参数了
        // Create a camera view matrix
        Matrix.setLookAtM(
            vMatrix, 0, 0f, 0f, 6.8f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f
        )
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        Log.i(TAG, "onSurfaceChanged() - ${width}x${height}")

        val ratio: Float = width.toFloat() / height.toFloat()
        // create a projection matrix from device screen geometry
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(p0: GL10?) {
        // Combine the projection and camera view matrices
        Matrix.multiplyMM(vPMatrix, 0, projMatrix, 0, vMatrix, 0)
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, vPMatrix, 0)

        drawFrame()
    }
}