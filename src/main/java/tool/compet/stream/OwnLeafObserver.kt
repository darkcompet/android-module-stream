/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs

/**
 * This is lowest (leaf) observer at first subscribe, so all events come to it will not be sent to down more.
 * From that, we can write log to observe events here.
 */
class OwnLeafObserver<T> : DkObserver<T> {
	override fun onSubscribe(controllable: DkControllable<*>) {
		if (BuildConfig.DEBUG) {
			__startTime = System.currentTimeMillis()
		}
	}

	override fun onNext(item: T?) {}

	override fun onError(e: Throwable) {
		if (BuildConfig.DEBUG) {
			DkLogs.error(this, e, "Stream error after %d (ms)", System.currentTimeMillis() - __startTime)
		}
	}

	override fun onComplete() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Stream complete after %d (ms)", System.currentTimeMillis() - __startTime)
		}
	}

	override fun onFinal() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Stream final after %d (ms)", System.currentTimeMillis() - __startTime)
			if (++__testFinalCount > 1) {
				DkLogs.warning(this, "Wrong implementation of #onFinal. Please review code !")
			}
		}
	}

	private var __testFinalCount = 0
	private var __startTime: Long = 0
}
