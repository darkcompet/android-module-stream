/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * God observable node.
 */
internal class MyGodControllableObservable<M>(
	private val controllable: DkControllable<M>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		OwnGodControllableObserver(observer, controllable).start()
	}
}
