/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable

/**
 * God observable node.
 * When start, this run callable and then handle next events.
 */
internal class MyGodCallableObservable<M>(
	private val execution: DkCallable<M>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		OwnGodCallableObserver(observer, execution).start()
	}
}
