/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

class OwnOnSubscribeObservable<M>(
	private val parent: DkObservable<M>,
	private val action: DkRunnable1<DkControllable<*>>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		parent.subscribe(OwnOnSubscribeObserver(observer, action))
	}
}
