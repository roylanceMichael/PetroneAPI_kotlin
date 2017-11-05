package kr.co.byrobot.petroneapi.Wifi.selector

import kotlinx.coroutines.experimental.*
import kr.co.byrobot.petroneapi.Wifi.*
import java.nio.channels.*
import java.nio.channels.spi.*

abstract class SelectorManagerSupport internal constructor() : SelectorManager {
  final override val provider: SelectorProvider = SelectorProvider.provider()

  protected suspend fun select(s: Selectable, op: SelectInterest,
      publishInterest: (Selectable) -> Unit) {
    require(s.interestedOps and op.flag != 0)

    suspendCancellableCoroutine<Unit> { c ->
      s.suspensions.addSuspension(op, c)
      c.disposeOnCancel(s)

      if (!c.isCancelled) {
        publishInterest(s)
      }
    }
  }

  protected fun handleSelectedKey(key: SelectionKey) {
    try {
      val readyOps = key.readyOps()
      val interestOps = key.interestOps()

      val subj = key.subject
      if (subj == null) {
        key.cancel()
      } else {
        for (i in SelectInterest.values()) {
          if (i.flag and readyOps != 0) {
            subj.suspensions.invokeIfPresent(i) { resume(Unit) }
          }
        }

        val newOps = interestOps and readyOps.inv()
        if (newOps != interestOps) {
          key.interestOps(newOps)
        }
      }
    } catch (t: Throwable) {
      // cancelled or rejected?
      key.cancel()
      key.subject?.let { subj ->
        cancelAllSuspensions(subj, t)
        key.subject = null
      }
    }
  }

  protected fun applyInterest(selector: Selector, attachment: Selectable) {
    val channel = attachment.channel

    val existingKey = channel.keyFor(selector)
    val existingAttachment = existingKey?.subject

    if (existingKey == null) {
      try {
        channel.register(selector, attachment.interestedOps, attachment)
      } catch (t: Throwable) {
        cancelAllSuspensions(attachment, t)
      }
    } else if (existingAttachment == null) {
      existingKey.cancel()
      notifyClosedImpl(selector, existingKey, attachment)
    } else {
      try {
        existingKey.interestOps(attachment.interestedOps)
      } catch (t: Throwable) {
        cancelAllSuspensions(attachment, t)
      }
    }
  }

  protected fun notifyClosedImpl(selector: Selector, key: SelectionKey, attachment: Selectable) {
    cancelAllSuspensions(attachment, ClosedChannelException())

    key.subject = null
    selector.wakeup()
  }

  protected fun cancelAllSuspensions(attachment: Selectable, t: Throwable) {
    attachment.suspensions.invokeForEachPresent {
      try {
        resumeWithException(t)
      } catch (t2: Throwable) { // rejected?
        t2.printStackTrace()
      }
    }
  }
}