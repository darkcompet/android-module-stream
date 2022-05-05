/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs

/**
 * @param <M> Model type.
 */
open class OwnObserver<M>(protected val child: DkObserver<M>) : DkObserver<M> {
	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		child.onSubscribe(controllable)
	}

	@Throws(Exception::class)
	override fun onNext(item: M?) {
		child.onNext(item)
	}

	override fun onError(e: Throwable) {
		child.onError(e)
	}

	@Throws(Exception::class)
	override fun onComplete() {
		child.onComplete()
	}

	override fun onFinal() {
		child.onFinal()
		if (BuildConfig.DEBUG) {
			if (++__testFinalCount > 1) {
				DkLogs.warning(this, "Wrong implementation of #onFinal. Please review code !")
			}
		}
	}

	private var __testFinalCount = 0
}
