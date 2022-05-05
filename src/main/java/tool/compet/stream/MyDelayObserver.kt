/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import android.os.Handler
import android.os.HandlerThread

internal class MyDelayObserver<T>(
	child: DkObserver<T>,
	private val delayMillis: Long
) : OwnObserver<T>(child) {

	override fun onNext(item: T?) {
		val handlerThread = HandlerThread(MyDelayObservable::class.java.name)
		handlerThread.start()

		val handler = Handler(handlerThread.looper)
		handler.postDelayed({
			try {
				child.onNext(item)
			}
			catch (e: Exception) {
				child.onError(e)
			}
			finally {
				handlerThread.quit()
			}
		}, delayMillis)
	}
}
