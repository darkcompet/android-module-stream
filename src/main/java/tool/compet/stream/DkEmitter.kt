/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * This class is helpful for some cases you wanna full-customize logic of emitting events,
 * so we just invoke [DkEmitter.call] without try/catch block to give you full-control.
 * Note for the implementation time:
 *
 *
 * You must handle all below event-methods
 * [DkObserver.onSubscribe],
 * [DkObserver.onNext],
 * [DkObserver.onError],
 * [DkObserver.onComplete],
 * [DkObserver.onFinal].
 * Normally, just use try-catch with finally statement to notify child observer.
 */
interface DkEmitter<T> {
	fun call(observer: DkObserver<T>)
}
