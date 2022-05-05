/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */
package tool.compet.stream

/**
 * Observerの関数の呼び出しを自由に管理したい場合、このクラスを利用して頂ければ、
 * Observerを渡しますので、イベントの処理を完全に支配できます。
 */
internal class MyEmitterObservable<M>(
	private val emitter: DkEmitter<M>
) : DkObservable<M>() {

	@Throws(Exception::class)
	override fun subscribeActual(observer: DkObserver<M>) {
		emitter.call(observer)
	}
}
