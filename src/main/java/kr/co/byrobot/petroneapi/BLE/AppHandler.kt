package kr.co.byrobot.petroneapi.BLE

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference



/**
 * Created by byrobot on 2017. 7. 18..
 */
open class AppHandler<T> : Handler {
    private var mReference: WeakReference<T>? = null
    protected var mMessageListener: HandleMessageListener<T>? = null

    constructor( reference: T ) {
        this.mReference = WeakReference<T>(reference)
    }

    constructor( reference: T, listener: HandleMessageListener<T> ) {
        this.mReference = WeakReference<T>(reference)
        this.mMessageListener = listener
    }

    override fun handleMessage(msg: Message) {
        val reference = mReference?.get()
        if (reference != null && mMessageListener != null) {
            mMessageListener!!.onHandleMessage(reference, msg)
        }
    }

    fun setMessageListener(messageListener: HandleMessageListener<T>) {
        this.mMessageListener = messageListener
    }

    fun getMessageListener(): HandleMessageListener<T> {
        return mMessageListener!!
    }


    interface HandleMessageListener<T> {
        fun onHandleMessage(reference: T, msg: Message)
    }
}