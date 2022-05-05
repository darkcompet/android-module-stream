/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import java.util.concurrent.Callable

/**
 * Extends this class to switch from upper-stream to lower-stream.
 *
 * @param <M> model-type of upper stream
 * @param <N> model-type of lower stream
</N></M> */
abstract class OwnBaseMapStreamObserver<M, N> protected constructor(
	protected var child: DkObserver<N>
) : OwnControllable(), Callable<N>, DkObserver<M> {

	@Throws(Exception::class)
	override fun call(): N {
		throw RuntimeException("Must implement this method")
	}

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		parent = controllable
		child.onSubscribe(controllable)
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
