/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

class OwnOnErrorObservable<M>(
	private val parent: DkObservable<M>,
	private val action: DkRunnable1<Throwable>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		parent.subscribe(OwnOnErrorObserver(observer, action))
	}
}
