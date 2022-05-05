/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

class OwnIgnoreErrorObserver<M>(child: DkObserver<M>) : OwnObserver<M>(child) {
	override fun onError(e: Throwable) {
		try {
			// We don't know what result was passed from us parent node
			// -> Ignore error and try to pass with null-result to child node
			child.onNext(null)
		}
		catch (exception: Exception) {
			// Humh, still error occured in child node...
			// -> We have no idea to resolve it
			// -> Just pass to child node exception which was caused from it
			child.onError(exception)
		}
	}
}
