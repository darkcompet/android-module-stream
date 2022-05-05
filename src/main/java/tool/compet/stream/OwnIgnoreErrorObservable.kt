/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * Imagine that some where in stream has occured exception at some node,
 * but we wanna ignore that error and continue to process it,
 *
 *
 * To resolve this, we introduce this node to switch to `onNext()` at `onError()` event.
 * So child node
 */
class OwnIgnoreErrorObservable<M>(
	private val parent: DkObservable<M>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		parent.subscribe(OwnIgnoreErrorObserver(observer))
	}
}
