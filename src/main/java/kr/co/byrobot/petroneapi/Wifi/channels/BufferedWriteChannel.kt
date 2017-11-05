package kr.co.byrobot.petroneapi.Wifi.channels

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder

/**
 * Created by byrobot on 2017. 7. 5..
 */
abstract class BufferedWriteChannel internal constructor(val pool: Channel<ByteBuffer>,
    order: ByteOrder) : WriteChannel {

  private var buffer: ByteBuffer = Empty
  var order: ByteOrder = order
    set(newValue) {
      field = newValue
      buffer.order(newValue)
    }

  val capacity: Int
    get() = buffer.capacity()

  val remaining: Int
    get() = buffer.remaining()

  fun putByte(value: Byte) {
    buffer.put(value)
  }

  fun putBytes(array: ByteArray) {
    buffer.put(array)
  }

  fun putBuffer(bb: ByteBuffer) {
    buffer.put(bb)
  }

  fun putUByte(value: Int) {
    require(value <= 0xff)
    buffer.put(value.toByte())
  }

  fun putShort(value: Short) {
    buffer.putShort(value)
  }

  fun putUShort(value: Int) {
    require(value >= 0) { "value shouldn't be negative" }
    require(value <= 0xffff) { "value is too big $value" }

    buffer.putShort(value.toShort())
  }

  fun putInt(value: Int) {
    buffer.putInt(value)
  }

  fun putUInt(value: Long) {
    buffer.putInt(value.toInt())
  }

  fun putLong(value: Long) {
    buffer.putLong(value)
  }

  fun putFloat(value: Float) {
    buffer.putFloat(value)
  }

  fun putDouble(value: Double) {
    buffer.putDouble(value)
  }

  suspend fun putString(s: String, charset: Charset) {
    putString(s, charset.newEncoder())
  }

  suspend fun putString(s: String, encoder: CharsetEncoder) {
    val from = CharBuffer.wrap(s)
    encoder.reset()

    while (from.hasRemaining()) {
      if (buffer.hasRemaining()) {
        encoder.encode(from, buffer, true)
      }

      if (from.hasRemaining()) {
        flush()
      }
    }
  }

  suspend override fun write(src: ByteBuffer) {
    while (src.hasRemaining()) {
      if (src.remaining() <= buffer.remaining()) {
        buffer.put(src)
      } else {
        while (src.hasRemaining() && buffer.hasRemaining()) {
          buffer.put(src.get())
        }
      }

      if (!buffer.hasRemaining()) {
        flush()
      }
    }
  }

  /**
   * Flushes all outstanding bytes, releases buffer and delegates to parent's [shutdownOutput]
   */
  override fun shutdownOutput() {
    runBlocking {
      flush()
      reset()

      shutdownImpl()
    }
  }

  /**
   * Ensures that the internal buffer has at least [required] bytes free.
   * If not then flush is triggered.
   * Throws an exception if [required] is too large
   */
  suspend fun ensureCapacity(required: Int) {
    if (buffer.remaining() < required) {
      flush()
    }
    if (buffer.remaining() < required) {
      throw IllegalArgumentException("required capacity couldn't be satisfied: $required")
    }
  }

  suspend fun flush() {
    val bb = buffer

    bb.flip()
    if (bb.hasRemaining()) {
      doWrite(bb)
      newBuffer()
    } else if (bb === Empty) {
      newBuffer()
    } else {
      bb.clear()
    }
  }

  /**
   * Releases internal buffer. All outstanding bytes are discarded.
   */
  fun reset() {
    val bb = buffer

    if (bb !== Empty) {
      buffer = Empty
      pool.offer(bb)
    }
  }

  protected abstract suspend fun doWrite(buffer: ByteBuffer)
  protected abstract fun shutdownImpl()

  private suspend fun newBuffer() {
    buffer = pool.receive().apply {
      clear()
      order(order)
    }
  }

  companion object {
    private val Empty = ByteBuffer.allocate(0)!!
  }
}