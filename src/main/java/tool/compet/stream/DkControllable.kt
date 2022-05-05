/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkUtils
import java.util.concurrent.Callable

/**
 * This node can pause, resume, cancel the task.
 */
open class DkControllable<T>(
	protected val child: DkObserver<T>
) : OwnControllable(), Callable<T>, DkObserver<T> {

	override fun call(): T {
		throw RuntimeException("Must implement this method")
	}

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		if (controllable === this) {
			DkUtils.complainAt(this, "Wrong implementation ! God observer must be parentless")
		}
		this.parent = controllable
		this.child.onSubscribe(controllable)
	}

	@Throws(Exception::class)
	override fun onNext(item: T?) {
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
	}
}
