/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

/**
 * This node just execute `Runnable` from caller and pass the result from above-node to under-node.
 */
class OwnOnNextObservable<M>(
	private val parent: DkObservable<M>,
	private val action: DkRunnable1<M>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		// Just send to parent own wrapped observer
		parent.subscribe(OwnOnNextObserver(observer, action))
	}
}
