/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class OwnSchedulerInObserver<M>(
	child: DkObserver<M>,
	private val scheduler: DkScheduler<M>
) : DkControllable<M>(child) {

	var task: Callable<M>? = null

	@Throws(Exception::class)
	fun start(parent: DkObservable<M>, delay: Long, timeUnit: TimeUnit, isSerial: Boolean) {
		// Give to children a chance to cancel scheduling in other thread
		child.onSubscribe(this)
		if (isCancel) {
			isCanceled = true
			onFinal()
			return
		}

		// Task in the service (IO thread...)
		val task = Callable<M> {
			// maybe it takes long time to schedule, so check again cancel event from user
			if (isCancel) {
				isCanceled = true
				onFinal()
			}
			else {
				parent.subscribe(this@OwnSchedulerInObserver)
			}
			null
		}

		// Start subscribe in other thread
		this.task = task
		scheduler.schedule(task, delay, timeUnit, isSerial)
	}

	override fun cancel(mayInterruptThread: Boolean): Boolean {
		var ok = super.cancel(mayInterruptThread)
		val task = this.task
		if (task != null) {
			ok = ok or scheduler.cancel(task, mayInterruptThread)
		}
		isCanceled = ok
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Cancelled task: $task, ok: $ok")
		}
		return ok
	}
}
