/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable

class OwnOnFinalObservable<M>(
	private val parent: DkObservable<M>,
	private val action: DkRunnable
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		parent.subscribe(OwnOnFinalObserver(observer, action))
	}
}
