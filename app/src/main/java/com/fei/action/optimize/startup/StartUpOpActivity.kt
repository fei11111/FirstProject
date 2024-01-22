package com.fei.action.optimize.startup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityStartUpOpBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 启动优化
 *
 * 1.任务调度优化：线程 + CPU 提升任务调度优先级
 *  1.1调整主线程优先级
 *      主线程的优先级调整很简单，直接在 Application 的 attachBaseContext() 调用 Process.setThreadPriority(-19)，将主线程设置为最高级别优先级即可。
 *  1.2调整渲染线程优先级
 */
class StartUpOpActivity : BaseActivity<EmptyViewModel, ActivityStartUpOpBinding>() {

    private var renderThreadTid: Int = 0
    private var maxFreqCPUIndex: Int = 0

    private lateinit var timer: Timer
    private var beforeCpuTime = 0f

    private val random = Random()
    private var num: Int = 0
    private val handler = object : Handler(Looper.getMainLooper()) {
        // 模拟 cpu 计算消耗
        override fun handleMessage(msg: Message) {
            for (i in 0 until 100_0000) {
                num += i
            }
            val time = random.nextInt(5000).toLong()
            Log.v("@@@", "延迟 time = $time")
            sendEmptyMessageDelayed(0, time)
        }
    }
    override fun createObserver() {
        //获取渲染线程
        mBinding.btnGetRender.setOnClickListener {
            renderThreadTid = getRenderThreadId()
            Log.i("@@@", " renderThreadId = $renderThreadTid")
        }
        //获取cpu最大核
        mBinding.btnGetCpu.setOnClickListener {
            maxFreqCPUIndex = getMaxFreqCPUIndex()
            Log.i("@@@", " maxFreqCPUIndex = $maxFreqCPUIndex")
        }

        // 线程绑定大核，返回 0 表示成功，否则失败
        /**
         * 获取时钟频率最高（即性能最好）的 CPU 核序列
         *
         * 获取需要绑定的线程 pid
         *
         * 调用 shced_setaffinity 函数将线程绑定到大核
         */
        mBinding.btnBinding.setOnClickListener {
            //线程提高等级
            android.os.Process.setThreadPriority(renderThreadTid,-19)
            //线程绑定大核
            val result = bindMaxFreqCore(maxFreqCPUIndex, renderThreadTid)
            Log.v("@@@", "result = $result")
        }

        mBinding.btnOperater.setOnClickListener {
            val time = random.nextInt(5000).toLong()
            Log.v("@@@", "延迟 time = $time")
            handler.sendEmptyMessageDelayed(0, time)

            timer = Timer()
            timer.schedule(object: TimerTask() {
                override fun run() {
                    val cpuTime = getCpuTime()
                    // 计算单位时间内的 CPU 速率
                    val cpuSpeed = (cpuTime - beforeCpuTime) / 5f
                    Log.v("@@@", "cpuSpeed = $cpuSpeed")
                    if (cpuSpeed <= 0.1) {
                        Log.v("@@@", "cpu is idle")
                    }
                    beforeCpuTime = cpuTime
                }
            }, 0, 5 * 1000)
        }

    }

    /**
     * 获取cpu最大核
     */
    private fun getMaxFreqCPUIndex(): Int {
        //1.可以通过 /sys/devices/system/cpu/ 目录下的文件查看当前设备有几个 CPU
        val cores = getNumberOfCpuCores()
        Log.i("@@@", "cors = $cores")
        //2.其中一个 cpu 目录的 cpu{x}/cpufreq/ 目录的 /cpuinfo_max_freq 可以查看该 cpu 的时钟周期频率
        if (cores == 0) return -1
        var maxFreq = -1
        var maxFreqCPUIndex = 0
        for (i in 0 until cores) {
            val filename = "/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq"
            val cpuInfoMaxFreqFile = File(filename)
            if (!cpuInfoMaxFreqFile.exists()) continue

            val buffer = ByteArray(128)
            FileInputStream(cpuInfoMaxFreqFile).use { stream ->
                stream.read(buffer)

                var endIndex = 0
                while (buffer[endIndex].toInt().toChar() in '0'..'9') endIndex++

                val freqBound = String(buffer, 0, endIndex).toInt()
                if (freqBound > maxFreq) {
                    maxFreq = freqBound
                    maxFreqCPUIndex = i
                }
            }
        }
        return maxFreqCPUIndex
    }

    /**
     * 获取cpu数量
     */
    private fun getNumberOfCpuCores(): Int {
        val file = File("/sys/devices/system/cpu/").listFiles()
        var size = 0
        file.forEach { file ->
            val path = file.name
            if (path.startsWith("cpu")) {
                val chars = path.toCharArray()
                //获取文件名字，如果文件名是cpu + 数字就是
                for (i in 3 until path.length) {
                    if (chars[i] in '0'..'9') {
                    }
                    size++
                }
            }
        }
        return size
    }

    /**
     * 获取渲染线程id
     */
    private fun getRenderThreadId(): Int {
        //查看应用的所有线程信息
        val appAllThreadMsgDir = File("/proc/${android.os.Process.myPid()}/task/")
        if (!appAllThreadMsgDir.isDirectory) return -1
        val files = appAllThreadMsgDir.listFiles() ?: arrayOf()
        var result = -1
        //接着查看目录线程的 stat 节点，就能具体查看线程的详细信息了，比如 tid、name 等
        //只需要遍历 /proc/pid/task 目录下的所有目录，查看 pid/stat 文件
        files.forEach { file ->
            val br = BufferedReader(FileReader("${file.path}/stat"), 100)
            val cpuRate = br.use {
                return@use br.readLine()
            }
            if (!cpuRate.isNullOrEmpty()) {
                val param = cpuRate.split(" ")
                val threadName = param[1]
                if (threadName == "(RenderThread)") {
                    result = param[0].toInt()
                    return@forEach
                }
            }
        }
        return result
    }

    /**
     * 配置CPU线程池
     */
    private fun getCpuThreadPool(): ThreadPoolExecutor {
        //获取cpu核数
        val corePoolSize = Runtime.getRuntime().availableProcessors()
        return ThreadPoolExecutor(
            corePoolSize, corePoolSize,
            0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(64),
            object : ThreadFactory {
                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "cpu-thread")
                }
            }
        )
    }

    /**
     * 配置IO线程池
     */
    private fun getIoThreadPool(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            0, 64,
            60, TimeUnit.SECONDS,
            SynchronousQueue(),
            object : ThreadFactory {
                override fun newThread(r: Runnable): Thread {
                    val thread = Thread(r, "io-thread")
                    //提高线程级别
                    thread.priority = Thread.MAX_PRIORITY
                    return thread
                }
            }
        )
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {

    }

    /**
     * 绑定线程到最大核
     */
    private external fun bindMaxFreqCore(maxFreqCpuIndex: Int, pid: Int): Int

    /**
     * 单位时间内进程消耗的 CPU 时间
     */
    private external fun getCpuTime():Float

    companion object {
        // Used to load the 'demo' library on application startup.
        init {
            System.loadLibrary("demo")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}