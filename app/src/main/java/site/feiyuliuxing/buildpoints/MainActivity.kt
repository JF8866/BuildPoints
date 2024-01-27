package site.feiyuliuxing.buildpoints

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import site.feiyuliuxing.buildpoints.databinding.ActivityMainBinding
import site.feiyuliuxing.buildpoints.utils.ColorTemplate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun loadTestData() {
        // todo
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        var result = BuildPoints.buildCenterRadiatePoints(
            0.0f, 0.0f,
            screenWidth / 2f, 0f, 120f, 30f, 0
        )
        /*val result = BuildPoints.buildParallelPoints(
            0.0f, 0.0f,
            0.0f, screenHeight / 2f,
            screenWidth / 2f, 80f, 0f, 0
        )*/
        //绕原点逆时针旋转45度
        var resultPoints = BuildPoints.rotate(result.resultPoints, 0f, 0f, 45f)
        //下移
//        resultPoints = BuildPoints.shift(resultPoints, 0f, -100f)
        val scaledPoints = resultPoints.map { it / screenWidth }.toFloatArray()
//        Log.i(TAG, "resultPoints = " + resultPoints.joinToString(", "))

        //映射成小车坐标系中的坐标
        val resultPoints2 = BuildPoints.transCoordinate(
            floatArrayOf(-1f, 1f),
            floatArrayOf(1f, -1f),
            scaledPoints
        )
//        binding.glSurfaceView.addPoints(scaledPoints)
//        binding.glSurfaceView.addPoints(resultPoints2)

        result = BuildPoints.buildImitationCirclePoints(
            200f, 500f, 200f, 10,
            floatArrayOf(100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f, 900f, 1000f),
            floatArrayOf(100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f, 900f, 1000f)
        )

        val pointList = mutableListOf<Float>()
        for (i in 0..result.resultPoints.size - 4 step 2) {
            val r = result.linesRadius[i / 2]
            if (r == 0f) {
                for (j in 0 until 4) {
                    pointList.add(result.resultPoints[i + j] / screenWidth)
                }
            } else {
                val arr = BuildPoints.buildCurvePoints(
                    result.resultPoints[i],
                    result.resultPoints[i + 1],
                    result.resultPoints[i + 2],
                    result.resultPoints[i + 3],
                    r, if(r < 200f) 5 else 10
                )
                for (f in arr) pointList.add(f / screenWidth)
            }
        }
        binding.glSurfaceView.addPoints(pointList.toFloatArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTestData()

        ColorTemplate.printColors()
        // Example of a call to a native method
    }

}