/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable

class OwnOnCompleteObserver<M>(child: DkObserver<M>, val action: DkRunnable) : OwnObserver<M>(child) {
	@Throws(Exception::class)
	override fun onComplete() {
		// Run action and pass complete-event to child node
		action.run()
		child.onComplete()
	}
}
