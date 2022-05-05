/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

// Own wrapped observer which holds child observer
class OwnOnNextObserver<M>(child: DkObserver<M>, val action: DkRunnable1<M>) : OwnObserver<M>(child) {
	@Throws(Exception::class)
	override fun onNext(item: M?) {
		// Execute action and Pass result to child node
		// Note that, God node will handle error if raised
		action.run(item)
		child.onNext(item)
	}
}
