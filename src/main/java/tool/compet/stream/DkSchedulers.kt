/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkExecutorService

object DkSchedulers {
	private var ioScheduler: DkScheduler<*>? = null
	private var uiScheduler: DkScheduler<*>? = null

	// Background thread scheduler
	fun <T> io(): DkScheduler<T> {
		if (ioScheduler == null) {
			synchronized(DkSchedulers::class.java) {
				if (ioScheduler == null) {
					ioScheduler = MyIoScheduler<Any>(DkExecutorService.getExecutor())
				}
			}
		}
		return ioScheduler as DkScheduler<T>
	}

	// Android ui thread scheduler
	fun <T> ui(): DkScheduler<T> {
		if (uiScheduler == null) {
			synchronized(DkSchedulers::class.java) {
				if (uiScheduler == null) {
					uiScheduler = MyUiScheduler<Any>()
				}
			}
		}
		return uiScheduler as DkScheduler<T>
	}
}
