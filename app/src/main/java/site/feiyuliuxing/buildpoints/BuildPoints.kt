package site.feiyuliuxing.buildpoints

import android.util.Log

object BuildPoints {
    private const val TAG = "BuildPoints"

    //500个float，那就是250个点
    private const val RESULT_BUFFER_SIZE = 500

    /**
     * @param centerPoint 起点圆心坐标指针，指向一个长度2的数组
     * @param radius 圆半径
     * @param startDeg 起始角度，x轴正半轴为0度，逆时针为正方向
     * @param endDeg 结束角度，x轴正半轴为0度，逆时针为正方向
     * @param deltaDeg 两条直线间的夹角
     * @param trackMode 是否重走返回再走下一条直线 0为不重走，1为重走
     * @param resultPoints 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
     */
    private external fun buildCenterRadiatePoints(
        centerPoint: FloatArray,
        radius: Float,
        startDeg: Float,
        endDeg: Float,
        deltaDeg: Float,
        trackMode: Int,
        resultPoints: FloatArray
    ): Int

    fun buildCenterRadiatePoints(
        centerPoint: FloatArray,
        radius: Float,
        startDeg: Float,
        endDeg: Float,
        deltaDeg: Float,
        trackMode: Int,
    ): FloatArray {
        val resultBuffer = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildCenterRadiatePoints(
            centerPoint,
            radius,
            startDeg,
            endDeg,
            deltaDeg,
            trackMode,
            resultBuffer
        )
        Log.i(TAG, "顶点个数: $pointCount")
        //一个点用2个float表示
        return resultBuffer.copyOfRange(0, pointCount * 2)
    }

    /**
     * @param startPoint 起点坐标指针，指向一个长度2的数组
     * @param endPoint 终点坐标指针，指向一个长度2的数组
     * @param distance 直线长度
     * @param interval 直线间隔
     * @param directionDeg 直线方向，x轴正半轴为0度，逆时针为正方向
     * @param trackMode 是否重走返回再走下一条直线 0为不重走，1为重走
     * @param resultPoints 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
     */
    private external fun buildParallelPoints(
        startPoint: FloatArray,
        endPoint: FloatArray,
        distance: Float,
        interval: Float,
        directionDeg: Float,
        trackMode: Int,
        resultPoints: FloatArray
    ): Int

    fun buildParallelPoints(
        startPoint: FloatArray,
        endPoint: FloatArray,
        distance: Float,
        interval: Float,
        directionDeg: Float,
        trackMode: Int
    ): FloatArray {
        val resultBuffer = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildParallelPoints(
            startPoint,
            endPoint,
            distance,
            interval,
            directionDeg,
            trackMode,
            resultBuffer
        )
        Log.i(TAG, "buildParallelPoints() - 顶点个数: $pointCount")
        //一个点用2个float表示
        return resultBuffer.copyOfRange(0, pointCount * 2)
    }


    /**
     * @param left1Point 车轮廓左后角，左侧1米点在机器小车中的坐标，指向一个长度2的数组
     * @param right1Point 车轮廓右后角，右侧1米点在机器小车中的坐标，指向一个长度2的数组
     * @param inputPoints 标准坐标系中生成的点坐标
     * @param resultPoints 接收转换后机器小车坐标系中的点坐标
     */
    private external fun transCoordinate(
        left1Point: FloatArray,
        right1Point: FloatArray,
        inputPoints: FloatArray,
        resultPoints: FloatArray
    ): Int

    fun transCoordinate(
        left1Point: FloatArray,
        right1Point: FloatArray,
        inputPoints: FloatArray
    ): FloatArray {
        val resultBuffer = FloatArray(inputPoints.size)
        transCoordinate(left1Point, right1Point, inputPoints, resultBuffer)
        return resultBuffer
    }

    /**
     * @param inputPoints 标准坐标系中生成的点坐标
     * @param resultPoints 接收转换后机器小车坐标系中的点坐标
     * @param cx 旋转中心点x坐标
     * @param cy 旋转中心点y坐标
     * @param thetaDeg 旋转角度，逆时针为正
     */
    private external fun rotate(
        inputPoints: FloatArray,
        resultPoints: FloatArray,
        cx: Float,
        cy: Float,
        thetaDeg: Float
    ): Int

    fun rotate(
        inputPoints: FloatArray,
        cx: Float,
        cy: Float,
        thetaDeg: Float
    ): FloatArray {
        val resultBuffer = FloatArray(inputPoints.size)
        rotate(inputPoints, resultBuffer, cx, cy, thetaDeg)
        return resultBuffer
    }

    /**
     * @param inputPoints 标准坐标系中生成的点坐标
     * @param resultPoints 接收转换后机器小车坐标系中的点坐标
     * @param xOffset x轴偏移量
     * @param yOffset y轴偏移量
     */
    private external fun shift(
        inputPoints: FloatArray,
        resultPoints: FloatArray,
        xOffset: Float,
        yOffset: Float
    ): Int

    fun shift(
        inputPoints: FloatArray,
        xOffset: Float,
        yOffset: Float
    ): FloatArray {
        val resultBuffer = FloatArray(inputPoints.size)
        shift(inputPoints, resultBuffer, xOffset, yOffset)
        return resultBuffer
    }

    init {
        System.loadLibrary("buildpoints")
    }
}