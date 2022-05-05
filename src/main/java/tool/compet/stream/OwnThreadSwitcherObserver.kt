/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs
import tool.compet.core.DkRunnable1
import java.util.concurrent.TimeUnit

class OwnThreadSwitcherObserver<T>(
	child: DkObserver<T>,
	private val scheduler: DkScheduler<T>,
	private val action: DkRunnable1<T>?,
	private val delay: Long,
	private val timeUnit: TimeUnit,
	private val isSerial: Boolean
) : OwnObserver<T>(child) {
	// Error when pass event to child node in other (main) thread
	private var eventCallException: Throwable? = null

	// Normally, this is run in IO thread
	@Throws(Exception::class)
	override fun onSubscribe(controllable: DkControllable<*>) {
		scheduler.schedule(Runnable {
			try {
				child.onSubscribe(controllable)
			}
			catch (e: Exception) {
				// Remember this error and pass to child node at next event
				eventCallException = e
			}
		}, delay, timeUnit, isSerial)
	}

	// Normally, this is run in IO thread
	@Throws(Exception::class)
	override fun onNext(item: T?) {
		scheduler.schedule(Runnable {
			// If no error occured, then pass result to child node
			if (eventCallException == null) {
				try {
					// Run action and pass result to child node
					action?.run(item)
					child.onNext(item)
				}
				catch (e: Exception) {
					// Remember this error and handle it at `onComplete()` event
					eventCallException = e
				}
			}
			else {
				DkLogs.error(this, eventCallException)
			}
		}, delay, timeUnit, isSerial)
	}

	// Normally, this is run in IO thread
	@Throws(Exception::class)
	override fun onComplete() {
		scheduler.scheduleNow(Runnable {
			if (eventCallException == null) {
				try {
					// No previous error -> just pass complete event to child node
					child.onComplete()
				}
				catch (e: Exception) {
					// Some exception was thrown at lower node.
					// -> We act as God node, switch to error event
					eventCallException = e
					child.onError(e)
				}
			}
			else {
				// Some exception was occured before -> we should pass that exception
				// to child node and then clear exception
				child.onError(eventCallException!!)
				eventCallException = null
			}
		}, isSerial)
	}

	// Normally, this is run in IO thread
	override fun onError(e: Throwable) {
		try {
			scheduler.scheduleNow(Runnable { child.onError(e) }, isSerial)
		}
		catch (other: Exception) {
			// Unable to switch thread -> just pass error event to child node
			child.onError(other)
		}
	}

	// Normally, this is run in IO thread
	override fun onFinal() {
		try {
			scheduler.scheduleNow(Runnable { child.onFinal() }, isSerial)
		}
		catch (e: Exception) {
			// Cannot schedule -> pass final-event to child node and write log
			child.onFinal()
			DkLogs.error(this, e)
		}
	}
}
