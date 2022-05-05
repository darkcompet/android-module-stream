/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable1

/**
 * This observer is flat map which can cancel, pause, resume stream
 *
 * @param <M> model-type of parent node
 * @param <N> model-type of child node
 */
class OwnFlatMapObserver<M, N>(
	child: DkObserver<N>,
	private val converter: DkCallable1<M, DkObservable<N>>
) : OwnBaseMapStreamObserver<M, N>(child) {

	@Throws(Exception::class)
	override fun onNext(item: M?) {
		// Use result from parent to create new stream
		// If we got null stream, then consider this as normal map
		// and pass null result to child node
		val nextStream: DkObservable<N>? = try {
			converter.call(item)
		}
		catch (e: Exception) {
			null
		}

		if (nextStream == null) {
			child.onNext(null)
		}
		else {
			// We consider this nextStream as a runner, so
			// if nextStream succeed, then we pass result to child node at throwIfFailureObserver,
			// if nextStream failed, then upper node will take care and handle it for us.
			val throwIfFailureObserver = OwnThrowIfFailureObserver(child)
			nextStream.subscribe(throwIfFailureObserver)
		}
	}
}
