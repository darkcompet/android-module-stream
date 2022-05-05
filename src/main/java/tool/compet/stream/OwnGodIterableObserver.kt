/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

class OwnGodIterableObserver<M>(child: DkObserver<M>) : DkControllable<M>(child), DkObserver<M> {
	fun start(items: Iterable<M>) {
		try {
			onSubscribe(this)
			if (isCancel) {
				isCanceled = true
				return
			}
			for (item in items) {
				if (isCancel) {
					isCanceled = true
					break
				}
				onNext(item)
			}
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
