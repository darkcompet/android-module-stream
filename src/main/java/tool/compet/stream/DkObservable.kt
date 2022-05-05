/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

import tool.compet.core.DkCallable
import tool.compet.core.DkCallable1
import tool.compet.core.DkRunnable
import tool.compet.core.DkRunnable1
import java.util.concurrent.TimeUnit

/**
 * This provides chained-method calling, called as stream or callback-system.
 * For detail, refer to RxJava or Java stream at https://github.com/ReactiveX/RxJava
 *
 * @param <M> Model which be passed down from parent node to child node.
 */
abstract class DkObservable<M> {
	companion object {
		/**
		 * Its useful if you wanna customize emitting-logic like onNext(), onError()... in #DkObserver to children.
		 * Note that, you must implement logic to call #onFinal() in observer.
		 */
		fun <M> fromEmitter(emitter: DkEmitter<M>): DkObservable<M> {
			return MyEmitterObservable(emitter)
		}

		/**
		 * Executes an action (without input from us) and then Emits result to child node.
		 *
		 *
		 * Make an execution without input, then pass result to lower node. Note that, you can cancel
		 * execution of running thread but cannot control (cancel, pause, resume...) it deeply.
		 * To overcome this, just use #withControllable() instead.
		 */
		fun <M> fromCallable(action: DkCallable<M>): DkObservable<M> {
			return MyGodCallableObservable(action)
		}

		/**
		 * Executes an action (without input from us) and then Emits result to child node.
		 *
		 *
		 * Its useful if you wanna control (pause, resume, cancel...) state of the task.
		 */
		fun <M> fromControllable(action: DkControllable<M>): DkObservable<M> {
			return MyGodControllableObservable(action)
		}

		/**
		 * Emits an item to child node.
		 */
		fun <M> from(item: M): DkObservable<M> {
			return MyGodArrayObservable(item)
		}

		/**
		 * Emits items to child node.
		 */
		fun <M> from(items: Array<M>): DkObservable<M> {
			return MyGodArrayObservable(items)
		}

		/**
		 * Emits items to child node.
		 */
		fun <M> from(items: Iterable<M>): DkObservable<M> {
			return MyGodIterableObservable(items)
		}
	}

	/**
	 * This map is used to switch model type.
	 */
	fun <N> map(function: DkCallable1<M, N>): DkObservable<N> {
		return OwnMapObservable(this, function)
	}

	/**
	 * This switched model type, is like with `map()`, but this maps with other stream.
	 */
	fun <N> flatMap(function: DkCallable1<M, DkObservable<N>>): DkObservable<N> {
		return OwnMapStreamObservable(this, function)
	}

	fun ignoreError(): DkObservable<M> {
		return OwnIgnoreErrorObservable(this)
	}

	/**
	 * Delay in given millis.
	 */
	@JvmOverloads
	fun delay(duration: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): DkObservable<M> {
		return MyDelayObservable(this, unit.toMillis(duration))
	}

	fun scheduleInBackground(): DkObservable<M> {
		return scheduleIn(DkSchedulers.io(), 0, TimeUnit.MILLISECONDS, false)
	}

	fun scheduleIn(scheduler: DkScheduler<M>, isSerial: Boolean): DkObservable<M> {
		return scheduleIn(scheduler, 0, TimeUnit.MILLISECONDS, isSerial)
	}

	@JvmOverloads
	fun scheduleIn(
		scheduler: DkScheduler<M>,
		delay: Long = 0,
		timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
		isSerial: Boolean = false
	): DkObservable<M> {
		return OwnScheduleInObservable(this, scheduler, delay, timeUnit, isSerial)
	}

	@JvmOverloads
	fun observeOn(
		scheduler: DkScheduler<M>,
		delayMillis: Long = 0L,
		unit: TimeUnit = TimeUnit.MILLISECONDS,
		isSerial: Boolean = true
	): DkObservable<M> {
		return OwnThreadSwitcherObservable(this, scheduler, null, delayMillis, unit, isSerial)
	}

	fun observeOnForeground(): DkObservable<M> {
		return observeOn(DkSchedulers.ui(), 0L, TimeUnit.MILLISECONDS, true)
	}

	fun scheduleInBackgroundAndObserveOnForeground(): DkObservable<M> {
		return this
			.scheduleIn(DkSchedulers.io(), 0L, TimeUnit.MILLISECONDS, false)
			.observeOn(DkSchedulers.ui(), 0L, TimeUnit.MILLISECONDS, true)
	}

	/**
	 * Make flow run in other thread, this is thread-switching.
	 */
	@JvmOverloads
	fun switchThread(
		scheduler: DkScheduler<M>,
		action: DkRunnable1<M>,
		delay: Long = 0,
		timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
		isSerial: Boolean = true
	): DkObservable<M> {
		return OwnThreadSwitcherObservable(this, scheduler, action, delay, timeUnit, isSerial)
	}

	/**
	 * Hears subscribe-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	fun doOnSubscribe(action: DkRunnable1<DkControllable<*>>): DkObservable<M> {
		return OwnOnSubscribeObservable(this, action)
	}

	/**
	 * Hears success-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	fun doOnNext(action: DkRunnable1<M>): DkObservable<M> {
		return OwnOnNextObservable(this, action)
	}

	/**
	 * Hears error-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	fun doOnError(action: DkRunnable1<Throwable>): DkObservable<M> {
		return OwnOnErrorObservable(this, action)
	}

	/**
	 * Hears complete-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	fun doOnComplete(action: DkRunnable): DkObservable<M> {
		return OwnOnCompleteObservable(this, action)
	}

	/**
	 * Hears final-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	fun doOnFinal(action: DkRunnable): DkObservable<M> {
		return OwnOnFinalObservable(this, action)
	}

	/**
	 * Subscribe a observer (listener, callback) to stream, so we can listen what happening in stream.
	 * Differ with aother subscribe() method, this will return Controllable object,
	 * so you can control (dispose, resume, pause...) stream anytime you want.
	 */
	@JvmOverloads
	fun subscribeForControllable(observer: DkObserver<M> = DkControllable(OwnLeafObserver())): DkControllable<M> {
		val controllable = DkControllable(observer)
		subscribe(controllable)
		return controllable
	}

	/**
	 * Subscribe with empty (leaf) observer (listener, callback) to stream.
	 */
	fun subscribe() {
		subscribe(OwnLeafObserver())
	}

	/**
	 * Subscribe with an observer (listener, callback) to stream, we can listen what happen in the stream.
	 *
	 * @param observer For first time, this is object passed from caller. Next, we wrap it to own observer
	 * and pass up to parent node.
	 */
	fun subscribe(observer: DkObserver<M>) {
		try {
			// Go up and pass the observer of this node to parent node.
			// By do it, we can make linked list of observers, so parent can pass events to this node later.
			subscribeActual(observer)
		}
		catch (e: Exception) {
			// Unable to subscribe (make node-link process), just pass error and then final events
			// to child observer
			observer.onError(e)
			observer.onFinal()
		}
	}

	/**
	 * Note for implementation time
	 *
	 * - For God node: implement logic of emitting events (#onNext, #onError, #onFinal...) to under node.
	 * The code should be blocked by try-catch to call #onFinal event.
	 *
	 * - For Godless node: just wrap given child observer and send to the upper node.
	 *
	 * The remain work to do is, write code in event-methods of Godless node.
	 * This job is same with implementation logic of God node. Mainly job is writing logic of #onNext,
	 * and if sometimes exception raised, you can call #onError to notify to lower node.
	 *
	 * @throws Exception When unable to subscribe at this node.
	 */
	@Throws(Exception::class)
	abstract fun subscribeActual(observer: DkObserver<M>)
}
