/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

internal class MyDelayObservable<M>(
	private val parent: DkObservable<M>,
	private val delayMillis: Long
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		this.parent.subscribe(MyDelayObserver(observer, delayMillis))
	}
}
