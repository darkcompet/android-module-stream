/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs
import tool.compet.core.DkRunnable

class OwnOnFinalObserver<M>(child: DkObserver<M>, val action: DkRunnable) : OwnObserver<M>(child) {
	override fun onFinal() {
		// Run action and pass final-event to child node
		// We will ignore any error since this is last event
		try {
			action.run()
		}
		catch (e: Exception) {
			DkLogs.error(this, e)
		}
		finally {
			child.onFinal()
		}
	}
}
