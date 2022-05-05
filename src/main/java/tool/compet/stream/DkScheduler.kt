/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * IoSchedulerとAndroidMainSchedulerの代表的サブクラスがこのインターフェースを実装しています。
 * サブクラス内では、HandlerやExecutorServiceを利用し、タスクをスケジューリングします。
 */
interface DkScheduler<T> {
	/**
	 * Schedule runnable task, default will be run on serial executor.
	 */
	@Throws(Exception::class)
	fun scheduleNow(task: Runnable)

	@Throws(Exception::class)
	fun scheduleNow(task: Runnable, isSerial: Boolean)

	@Throws(Exception::class)
	fun schedule(task: Runnable, delay: Long, unit: TimeUnit, isSerial: Boolean)

	/**
	 * Schedule callable task, default will be run on serial executor.
	 */
	@Throws(Exception::class)
	fun scheduleNow(task: Callable<T>)

	@Throws(Exception::class)
	fun scheduleNow(task: Callable<T>, isSerial: Boolean)

	@Throws(Exception::class)
	fun schedule(task: Callable<T>, delay: Long, unit: TimeUnit, isSerial: Boolean)

	/**
	 * Just try to cancel, not serious way to cancel a task.
	 * To cancel a task completely, lets implement cancel in Controllable.
	 */
	fun cancel(task: Callable<T>, mayInterruptThread: Boolean): Boolean
}
