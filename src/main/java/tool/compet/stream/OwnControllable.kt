/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkLogs

/**
 * Controllable observer which user can use it to cancel, resume, pause... current stream any time.
 * Since user normally control the stream in main thread, but the stream is executed IO thread almostly,
 * so we must
 */
abstract class OwnControllable {
	// Parent controllable
	protected var parent: DkControllable<*>? = null

	// Indicates child requested resume event
	@Volatile
	protected var isResume = false

	// Indicate this and parent have resumed succeed or not
	@Volatile
	var isResumed = false
		protected set

	// Indicates child requested pause event
	@Volatile
	protected var isPause = false

	// Indicates this and parent have paused succeed or not
	@Volatile
	var isPaused = false
		protected set

	// Indicates child requested cancel event
	@Volatile
	protected var isCancel = false

	// Indicate this and parent have cancelled succeed or not
	@Volatile
	var isCanceled = false
		protected set

	/**
	 * Subclass should overide if want to handle Resume event.
	 */
	@Synchronized
	fun resume(): Boolean {
		var ok = true
		isResume = true
		if (parent != null) {
			ok = parent!!.resume()
		}
		isResumed = ok
		return ok
	}

	/**
	 * Subclass should overide if want to handle Pause event.
	 */
	@Synchronized
	fun pause(): Boolean {
		var ok = true
		isPause = true
		if (parent != null) {
			ok = parent!!.pause()
		}
		isPaused = ok
		return ok
	}

	/**
	 * Subclass should overide if want to handle Cancel event.
	 */
	@Synchronized
	open fun cancel(mayInterruptThread: Boolean): Boolean {
		var ok = true
		isCancel = true
		if (parent != null) {
			ok = parent!!.cancel(mayInterruptThread)
		}
		if (BuildConfig.DEBUG) {
			DkLogs.info(
				this, "Cancelled with mayInterruptThread %b inside parent with result %b",
				mayInterruptThread, ok
			)
		}
		isCanceled = ok
		return ok
	}
}
