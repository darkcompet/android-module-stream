/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * God observable node.
 */
internal class MyGodArrayObservable<M> : DkObservable<M> {
	private val items: Iterable<M>

	constructor(item: M) {
		this.items = listOf(item)
	}

	constructor(items: Array<M>) {
		this.items = items.toList()
	}

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		OwnGodArrayObserver(observer).start(items)
	}
}
