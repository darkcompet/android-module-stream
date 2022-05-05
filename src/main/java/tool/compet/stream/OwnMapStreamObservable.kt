/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable1

/**
 * Like as normal map, this also maps result from parent node to other type at child node.
 * But this is mapping other stream, so it is convenient method for join streams and run
 * them as straight chain.
 *
 * @param <M> model-type of parent node
 * @param <N> model-type of child node
 */
class OwnMapStreamObservable<M, N>(
	private val parent: DkObservable<M>,
	private val converter: DkCallable1<M, DkObservable<N>>
) : DkObservable<N>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<N>) {
		parent.subscribe(OwnFlatMapObserver(observer, converter))
	}
}
