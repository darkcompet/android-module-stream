/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable

class OwnGodCallableObserver<M>(
	child: DkObserver<M>,
	private val callable: DkCallable<M>
) : DkControllable<M>(child) {

	fun start() {
		try {
			child.onSubscribe(this)
			if (isCancel) {
				isCanceled = true
				return
			}
			child.onNext(callable.call())
			child.onComplete()
		}
		catch (e: Exception) {
			child.onError(e)
		}
		finally {
			child.onFinal()
		}
	}

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		child.onSubscribe(controllable)
	}
}
