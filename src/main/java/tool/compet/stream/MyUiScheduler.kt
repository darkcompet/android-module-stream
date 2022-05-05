/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import android.os.Handler
import android.os.Looper
import tool.compet.core.DkLogcats
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

internal class MyUiScheduler<T> : DkScheduler<T> {
	private val handler: Handler
	private val pendingCommands: ConcurrentHashMap<Callable<T>, Runnable>

	init {
		handler = Handler(Looper.getMainLooper())
		pendingCommands = ConcurrentHashMap()
	}

	override fun scheduleNow(task: Runnable) {
		schedule(task, 0, TimeUnit.MILLISECONDS, true)
	}

	override fun scheduleNow(task: Runnable, isSerial: Boolean) {
		schedule(task, 0, TimeUnit.MILLISECONDS, isSerial)
	}

	override fun schedule(task: Runnable, delay: Long, unit: TimeUnit, isSerial: Boolean) {
		schedule(Callable {
			task.run()
			null
		}, delay, unit, isSerial)
	}

	override fun scheduleNow(task: Callable<T>) {
		schedule(task, 0, TimeUnit.MILLISECONDS, true)
	}

	override fun scheduleNow(task: Callable<T>, isSerial: Boolean) {
		schedule(task, 0, TimeUnit.MILLISECONDS, isSerial)
	}

	override fun schedule(task: Callable<T>, delay: Long, unit: TimeUnit, isSerial: Boolean) {
		// Run on IO thread, so must take care about thread-safe
		val command = Runnable {
			try {
				task.call()
			}
			catch (e: Exception) {
				DkLogcats.error(this, e)
			}
			finally {
				pendingCommands.remove(task)
			}
		}
		pendingCommands[task] = command
		handler.postDelayed(command, unit.toMillis(delay))
	}

	// Just try to cancel, not serious way to cancel a task
	override fun cancel(task: Callable<T>, mayInterruptThread: Boolean): Boolean {
		val command = pendingCommands[task]
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "Cancelled task %s, result: %b", task.toString(), command != null)
		}
		if (command != null) {
			handler.removeCallbacks(command)
			pendingCommands.remove(task)
			return true
		}
		return false
	}
}
