/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

class OwnOnSubscribeObserver<M>(child: DkObserver<M>, val action: DkRunnable1<DkControllable<*>>) :
	OwnObserver<M>(child) {

	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		// Run incoming task and pass controllable to child node
		action.run(controllable)
		child.onSubscribe(controllable)
	}
}
