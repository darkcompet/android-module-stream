/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable1

/**
 * @param <M> model-type of upper node.
 * @param <N> model-type of lower node.
 */
class OwnMapObservable<M, N>(
	private val parent: DkObservable<M>,
	private val converter: DkCallable1<M, N>
) : DkObservable<N>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<N>) {
		parent.subscribe(OwnMapObserver(observer, converter))
	}
}
