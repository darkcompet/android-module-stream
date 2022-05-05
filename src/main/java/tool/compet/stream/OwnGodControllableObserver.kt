/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

class OwnGodControllableObserver<M>(
	child: DkObserver<M>,
	val controllable: DkControllable<M>
) : DkControllable<M>(child), DkObserver<M> {

	fun start() {
		try {
			onSubscribe(this)
			if (isCancel) {
				isCanceled = true
				return
			}
			onNext(controllable.call())
			onComplete()
		}
		catch (e: Exception) {
			onError(e)
		}
		finally {
			onFinal()
		}
	}

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		child.onSubscribe(controllable)
	}
}
