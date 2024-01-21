package site.feiyuliuxing.buildpoints.glview

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import site.feiyuliuxing.buildpoints.BuildPoints
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private const val TAG = "MyGLRenderer"

class MyGLRenderer(context: Context) : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        uniform mat4 translateMatrix;
        uniform bool isTranslate;
        attribute vec4 vPosition;
        attribute vec2 aTextureCoord;
        varying vec2 vTextureCoord;
        void main() {
            if(isTranslate){
                gl_Position = uMVPMatrix * translateMatrix * vPosition;
            } else {
                gl_Position = uMVPMatrix * vPosition;
            }
            vTextureCoord = aTextureCoord;
        }
    """.trimIndent()

    private val fragmentShaderSource = """
        precision mediump float;
        uniform bool isTexture;
        uniform vec4 vColor;
        uniform sampler2D uTextureUnit;
        varying vec2 vTextureCoord;
        varying vec4 vFragColor;
        void main(void) {
            if(isTexture) {
                gl_FragColor = texture2D(uTextureUnit,vTextureCoord);
            } else {
                gl_FragColor = vColor;
            }
        }
    """.trimIndent()


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

    //画轨迹线使用点顶点
    private val vertexBuffer: FloatBuffer
    private val vertexBuffer2: FloatBuffer

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


    init {
        // todo
        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels
        Log.i(TAG, "屏幕尺寸: $screenWidth x $screenHeight")
        /*var resultPoints = BuildPoints.buildCenterRadiatePoints(
            floatArrayOf(0.0f, 0.0f),
            screenWidth / 2f, 0f, 120f, 30f, 0
        )*/
        var resultPoints = BuildPoints.buildParallelPoints(
            floatArrayOf(0.0f, 0.0f),
            floatArrayOf(0.0f, screenHeight / 2f),
            screenWidth / 2f, 80f, 0f, 0
        )
        //绕原点逆时针旋转45度
        resultPoints = BuildPoints.rotate(resultPoints, 0f, 0f, 45f)
        //下移
//        resultPoints = BuildPoints.shift(resultPoints, 0f, -100f)
        val scaledPoints = resultPoints.map { it / (screenWidth / 2f) }.toFloatArray()
//        Log.i(TAG, "resultPoints = " + resultPoints.joinToString(", "))
        vertexBuffer = ByteBuffer.allocateDirect(resultPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(scaledPoints)
            .apply { position(0) }

        //映射成小车坐标系中的坐标
        val resultPoints2 = BuildPoints.transCoordinate(
            floatArrayOf(-1f, 1f),
            floatArrayOf(1f, -1f),
            scaledPoints
        )
        vertexBuffer2 = ByteBuffer.allocateDirect(resultPoints2.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(resultPoints2)
            .apply { position(0) }
    }


    private fun compileShaders(): Int {
        val status = intArrayOf(0)

//        Log.i(TAG, vertexShaderCode)
//        Log.i(TAG, fragmentShaderSource)

        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexShaderCode)
        GLES20.glCompileShader(vertexShader)
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "顶点着色器编译失败：" + GLES20.glGetShaderInfoLog(vertexShader))
        }

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource)
        GLES20.glCompileShader(fragmentShader)
        GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "片段着色器编译失败：" + GLES20.glGetShaderInfoLog(fragmentShader))
        }

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == 0) {
            Log.e(TAG, "链接源程序失败：" + GLES20.glGetProgramInfoLog(program))
        }

        // Delete the shaders as the program has them now
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        return program
    }

    private fun drawFrame() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //下面两行代码，防止图片的透明部分被显示成黑色
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)//启用纹理坐标属性

        //画几何图形
        GLES20.glUniform1i(isTextureHandle, 0)
        GLES20.glUniform1i(isTranslateHandle, 0)

        drawLinesAndArrows(vertexBuffer, 0f, 0f, 0f)
        drawLinesAndArrows(vertexBuffer2, 1f, 0f, 0f)

        //画bitmap
//        GLES20.glUniform1i(isTextureHandle, 1)

//        GLES20.glUniformMatrix4fv(translateMatrixHandle, 1, false, translateMatrix, 0)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    private fun drawLinesAndArrows(dataBuffer: FloatBuffer, r: Float, g: Float, b: Float) {
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, dataBuffer)
        GLES20.glUniform4f(colorHandle, r, g, b, 1f)
        GLES20.glLineWidth(4f)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, dataBuffer.capacity() / 2)

        //todo 画线上表示方向的箭头
//        GLES20.glUniform1i(isTranslateHandle, 1)
        GLES20.glLineWidth(3f)
        for (i in 0..dataBuffer.capacity() - 4 step 2) {
            val x1 = dataBuffer.get(i)
            val y1 = dataBuffer.get(i + 1)
            val x2 = dataBuffer.get(i + 2)
            val y2 = dataBuffer.get(i + 3)
            val radians = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
            val degree = Math.toDegrees(radians).toFloat()

            //https://developer.android.com/reference/android/opengl/Matrix
            /*Matrix.setIdentityM(arrowMatrix, 0)
            Matrix.rotateM(arrowMatrix, 0, degree, 0f, 0f,0f)
            Matrix.translateM(arrowMatrix, 0, (x1 + x2) / 2f, (y1 + y2) / 2f, 0f)
            GLES20.glUniformMatrix4fv(translateMatrixHandle, 1, false, arrowMatrix, 0)*/

            var arr = BuildPoints.rotate(arrowVertex, 0f, 0f, degree)
            arr = BuildPoints.shift(arr, (x1 + x2) / 2f, (y1 + y2) / 2f)//在线段中间画箭头
//            arr = BuildPoints.shift(arr, x2, y2)//在线段尾部画箭头
            arrowVertexBuffer.apply {
                position(0)
                put(arr)
                position(0)
            }

            GLES20.glVertexAttribPointer(
                positionHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                8,
                arrowVertexBuffer
            )
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3)
        }
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.i(TAG, "onSurfaceCreated()")
        programHandle = compileShaders()
        GLES20.glUseProgram(programHandle)
        muMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        positionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition")
        textureCoordHandle = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        colorHandle = GLES20.glGetUniformLocation(programHandle, "vColor")
        isTextureHandle = GLES20.glGetUniformLocation(programHandle, "isTexture")
        translateMatrixHandle = GLES20.glGetUniformLocation(programHandle, "translateMatrix")
        isTranslateHandle = GLES20.glGetUniformLocation(programHandle, "isTranslate")

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
        GLES20.glViewport(0, 0, width, height)
        Log.i(TAG, "onSurfaceChanged() - ${width}x${height}")

        val ratio: Float = width.toFloat() / height.toFloat()
        // create a projection matrix from device screen geometry
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(p0: GL10?) {
        // Combine the projection and camera view matrices
        Matrix.multiplyMM(vPMatrix, 0, projMatrix, 0, vMatrix, 0)
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, vPMatrix, 0)

        drawFrame()
    }
}