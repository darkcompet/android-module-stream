/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

internal class MyIoScheduler<T>(
	private val parellelExecutor: ScheduledExecutorService
) : DkScheduler<T> {
	private val serialExecutor: SerialExecutor<T>
	private val schedulingTasks: ConcurrentHashMap<Callable<T>, ScheduledFuture<*>>
	private val pendingTasks: ConcurrentLinkedQueue<Callable<T>>

	init {
		schedulingTasks = ConcurrentHashMap<Callable<T>, ScheduledFuture<*>>()
		pendingTasks = ConcurrentLinkedQueue<Callable<T>>()
		serialExecutor = SerialExecutor(parellelExecutor, schedulingTasks, pendingTasks)
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
		if (isSerial) {
			// Note that, task will be auto queued to pendingTasks
			serialExecutor.schedule(task, delay, unit)
		}
		else {
			schedulingTasks[task] = parellelExecutor.schedule(task, delay, unit)
		}
	}

	// Just try to cancel, not serious way to cancel a task
	override fun cancel(task: Callable<T>, mayInterruptThread: Boolean): Boolean {
		val future = schedulingTasks[task]
		if (future == null) {
			pendingTasks.remove(task)
			return true
		}
		val ok = future.cancel(mayInterruptThread)
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Cancelled task %s, result: %b", task.toString(), ok)
		}
		if (ok) {
			schedulingTasks.remove(task)
			return true
		}
		return false
	}

	internal class SerialExecutor<T>(
		private val executor: ScheduledExecutorService,
		var schedulingTasks: ConcurrentHashMap<Callable<T>, ScheduledFuture<*>>,
		var pendingTasks: ConcurrentLinkedQueue<Callable<T>>
	) {
		var active: Callable<T>? = null

		@Synchronized
		fun schedule(task: Callable<T>, delay: Long, timeUnit: TimeUnit?) {
			pendingTasks.offer(task)
			// start schedule if have not active task
			if (active == null) {
				scheduleNext(delay, timeUnit)
			}
		}

		@Synchronized
		fun scheduleNext(delay: Long, timeUnit: TimeUnit?) {
			if (pendingTasks.poll().also { active = it } != null) {
				val future = executor.schedule({
					try {
						active!!.call()
					}
					catch (e: Exception) {
						DkLogs.warning(this, "Error when run on serial-executor for task: $active")
						DkLogs.error(this, e)
					}
					finally {
						// Cleanup for this task and schedule next
						schedulingTasks.remove(active)
						scheduleNext(delay, timeUnit)
					}
				}, delay, timeUnit)

				// Store this task to handle result or cancel if need
				schedulingTasks[active!!] = future
			}
		}
	}
}
