package site.feiyuliuxing.buildpoints

import android.util.Log

class BuildPointsResult(val resultPoints: FloatArray, val linesRadius: FloatArray)

object BuildPoints {
    private const val TAG = "BuildPoints"

    //500个float，那就是250个点
    private const val RESULT_BUFFER_SIZE = 500

    /**
     * @param cx 起点圆心X坐标
     * @param cy 起点圆心Y坐标
     * @param radius 圆半径
     * @param startDeg 起始角度，x轴正半轴为0度，逆时针为正方向
     * @param endDeg 结束角度，x轴正半轴为0度，逆时针为正方向
     * @param deltaDeg 两条直线间的夹角
     * @param trackMode 是否重走返回再走下一条直线 0为不重走，1为重走
     * @param resultPoints 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
     * @param linesRadius 接收 resultPoints 中每相邻两点所成线的曲率半径
     */
    private external fun buildCenterRadiatePoints(
        cx: Float, cy: Float,
        radius: Float,
        startDeg: Float,
        endDeg: Float,
        deltaDeg: Float,
        trackMode: Int,
        resultPoints: FloatArray,
        linesRadius: FloatArray
    ): Int

    fun buildCenterRadiatePoints(
        cx: Float, cy: Float,
        radius: Float,
        startDeg: Float,
        endDeg: Float,
        deltaDeg: Float,
        trackMode: Int,
    ): BuildPointsResult {
        val resultPoints = FloatArray(RESULT_BUFFER_SIZE)
        val linesRadius = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildCenterRadiatePoints(
            cx, cy,
            radius,
            startDeg,
            endDeg,
            deltaDeg,
            trackMode,
            resultPoints,
            linesRadius
        )
        Log.i(TAG, "顶点个数: $pointCount")
        //一个点用2个float表示
        return BuildPointsResult(
            resultPoints.copyOfRange(0, pointCount * 2),
            linesRadius.copyOfRange(0, pointCount - 1)
        )
    }

    /**
     * @param startX 起点X坐标指针
     * @param startY 起点Y坐标指针
     * @param endX 终点X坐标指针
     * @param endY 终点Y坐标指针
     * @param distance 直线长度
     * @param interval 直线间隔
     * @param directionDeg 直线方向，x轴正半轴为0度，逆时针为正方向
     * @param trackMode 是否重走返回再走下一条直线 0为不重走，1为重走
     * @param resultPoints 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
     * @param linesRadius 接收 resultPoints 中每相邻两点所成线的曲率半径
     */
    private external fun buildParallelPoints(
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        distance: Float,
        interval: Float,
        directionDeg: Float,
        trackMode: Int,
        resultPoints: FloatArray,
        linesRadius: FloatArray
    ): Int

    fun buildParallelPoints(
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        distance: Float,
        interval: Float,
        directionDeg: Float,
        trackMode: Int
    ): BuildPointsResult {
        val resultPoints = FloatArray(RESULT_BUFFER_SIZE)
        val linesRadius = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildParallelPoints(
            startX, startY,
            endX, endY,
            distance,
            interval,
            directionDeg,
            trackMode,
            resultPoints,
            linesRadius
        )
        Log.i(TAG, "buildParallelPoints() - 顶点个数: $pointCount")
        //一个点用2个float表示
        return BuildPointsResult(
            resultPoints.copyOfRange(0, pointCount * 2),
            linesRadius.copyOfRange(0, pointCount - 1)
        )
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


    /**
    input:
     * @param l0 车尾宽度
     * @param l1 车长度
     * @param l2 车头宽度
     * @param distanceNum 仿圆的圈数
     * @param xDistances x轴方向离车距离
     * @param yDistances y轴方向离车距离
    output:
     * @param resultPoints： 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
     * @param linesRadius： 接收每两个点间线的曲率半径，直线的曲率半径为零
     * @return 生成点的数量
     */
    private external fun buildImitationCirclePoints(
        l0: Float,
        l1: Float,
        l2: Float,
        distanceNum: Int,
        xDistances: FloatArray,
        yDistances: FloatArray,
        resultPoints: FloatArray,
        linesRadius: FloatArray
    ): Int

    fun buildImitationCirclePoints(
        l0: Float,
        l1: Float,
        l2: Float,
        distanceNum: Int,
        xDistances: FloatArray,
        yDistances: FloatArray
    ): BuildPointsResult {
        val resultPoints = FloatArray(RESULT_BUFFER_SIZE)
        val linesRadius = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildImitationCirclePoints(
            l0, l1, l2, distanceNum, xDistances, yDistances, resultPoints, linesRadius
        )
        Log.i(TAG, "buildImitationCirclePoints() - 顶点个数: $pointCount")
        //一个点用2个float表示
        return BuildPointsResult(
            resultPoints.copyOfRange(0, pointCount * 2),
            linesRadius.copyOfRange(0, pointCount - 1)
        )
    }

    /**
    input:
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param endX 终止点X坐标
     * @param endY 终止点Y坐标
     * @param lineRadius 曲率半径
     * @param num 需要生成的点数量
    output:
     * @param resultPoints 接收点坐标的一个足够大的数组，参考每条线两个点
     * @return 生成点的数量
     */
    private external fun buildCurvePoints(
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        lineRadius: Float, num: Int,
        resultPoints: FloatArray
    ): Int

    fun buildCurvePoints(
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        lineRadius: Float, num: Int
    ): FloatArray {
        val resultPoints = FloatArray(RESULT_BUFFER_SIZE)
        val pointCount = buildCurvePoints(startX, startY, endX, endY, lineRadius, num, resultPoints)
        return resultPoints.copyOfRange(0, pointCount * 2)
    }

    init {
        System.loadLibrary("buildpoints")
    }
}