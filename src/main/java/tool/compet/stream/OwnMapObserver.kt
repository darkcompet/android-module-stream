/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable1

/**
 * @param <I> Input model type.
 * @param <O> Output model type.
 */
class OwnMapObserver<I, O>(
	val child: DkObserver<O>,
	private val converter: DkCallable1<I, O>
) : DkObserver<I> {

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		child.onSubscribe(controllable)
	}

	@Throws(Exception::class)
	override fun onNext(item: I?) {
		child.onNext(converter.call(item))
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
