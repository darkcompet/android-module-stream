/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * This node ONLY take care about 2 events: onSubscribe(), onNext() and onError().
 * That is, other events like onComplete(), onFinal() will be ignored,
 * and not be sent down to child node.
 *
 * - At onSubscribe() event, this just send down controllable to child node.
 * - At onNext() event, this just send down model to child node.
 * - At onError() event, this will throw RuntimeException as follow up specification.
 *
 * @param <T> Model type.
 */
class OwnThrowIfFailureObserver<T>(child: DkObserver<T>) : OwnObserver<T>(child) {
	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		// Just send down to child subcription event
		child.onSubscribe(controllable)
	}

	@Throws(Exception::class)
	override fun onNext(item: T?) {
		// Just tell child success result
		child.onNext(item)
	}

	@Throws(Exception::class)
	override fun onComplete() {
		// This node ignores onComplete event
	}

	override fun onError(e: Throwable) {
		// Throws exception as specification so upper stream can take care it.
		throw RuntimeException(e)
	}

	override fun onFinal() {
		// This node ignores onFinal event
	}
}
