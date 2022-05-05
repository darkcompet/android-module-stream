/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1
import java.util.concurrent.TimeUnit

/**
 * Normally, switch thread, run in main thread instead of IO thread.
 */
class OwnThreadSwitcherObservable<M>(
	private val parent: DkObservable<M>,
	private val scheduler: DkScheduler<M>,
	private val action: DkRunnable1<M>?,
	private val delay: Long,
	private val timeUnit: TimeUnit,
	private val isSerial: Boolean
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		parent.subscribe(OwnThreadSwitcherObserver(observer, scheduler, action, delay, timeUnit, isSerial))
	}
}
