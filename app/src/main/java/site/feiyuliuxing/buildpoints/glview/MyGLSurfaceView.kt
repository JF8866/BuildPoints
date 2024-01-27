package site.feiyuliuxing.buildpoints.glview

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView : GLSurfaceView {
    private val renderer: MyGLRenderer

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
    }

    fun addPoints(points: FloatArray) {
        renderer.addPoints(points)
    }
}