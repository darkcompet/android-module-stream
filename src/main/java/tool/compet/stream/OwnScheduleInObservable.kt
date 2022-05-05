/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import java.util.concurrent.TimeUnit

/**
 * Normally, switch thread, and run code of upper nodes in IO (background) thread.
 */
class OwnScheduleInObservable<M>(
	private val parent: DkObservable<M>,
	private val scheduler: DkScheduler<M>,
	private val delay: Long,
	private val timeUnit: TimeUnit,
	private val isSerial: Boolean
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		// From now, code will be scheduled in other thread
		OwnSchedulerInObserver(observer, scheduler).start(parent, delay, timeUnit, isSerial)
	}
}
