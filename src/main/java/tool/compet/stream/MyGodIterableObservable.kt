/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * God observable node.
 */
internal class MyGodIterableObservable<M>(
	private val items: Iterable<M>?
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		OwnGodIterableObserver(observer).start(items!!)
	}
}
