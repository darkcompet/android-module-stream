/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkRunnable1

class OwnOnErrorObserver<M>(child: DkObserver<M>, val action: DkRunnable1<Throwable>) : OwnObserver<M>(child) {
	override fun onError(e: Throwable) {
		try {
			action.run(e)
			child.onError(e)
		}
		catch (other: Exception) {
			child.onError(other)
		}
	}
}
