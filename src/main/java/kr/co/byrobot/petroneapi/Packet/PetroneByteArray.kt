package kr.co.byrobot.petroneapi.Packet

import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * Created by byrobot on 2017. 6. 29..
 */
public class PetroneByteArray {
    private var array: ByteArray? = null
    private var cursor: Int = 0

    fun setCursor(cursor: Int) {
        this.cursor = cursor
    }

    operator fun get(index: Int = cursor): Byte {
        this.cursor += 1
        return this.array!!.get(index)
    }

    fun get16(index: Int = cursor): Short {
        var ret : Int = (this.array!!.get(index+1)).toInt()
        ret = (ret shl 8) + (this.array!!.get(index)).toInt()


        this.cursor += 2
        return ret.toShort()
    }

    fun get32(index: Int = cursor): Int {
        var ret : Int = (this.array!!.get(index+3)).toInt()
        ret = (ret shl 8) + (this.array!!.get(index+2)).toInt()
        ret = (ret shl 8) + (this.array!!.get(index+1)).toInt()
        ret = (ret shl 8) + (this.array!!.get(index)).toInt()


        this.cursor += 4
        return ret
    }

    fun get64(index: Int = cursor): Long {
        var ret : Long = (this.array!!.get(index+7)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+6)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+5)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+4)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+3)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+2)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index+1)).toLong()
        ret = (ret shl 8) + (this.array!!.get(index)).toLong()

        this.cursor += 8
        return ret
    }

    operator fun set(index: Int = -1, value: Byte) {
        if( index > -1) {
            this.array!!.set(index, value)
        } else {
            this.array!!.set(this.cursor++, value)
        }
    }

    operator fun set(index: Int = -1, value: PetroneDataType) {
        if( index > -1) {
            set(index, value.datatype.toByte());
        } else {
            set(this.cursor++, value.datatype.toByte());
        }
    }

    operator fun set(index: Int = -1, value: Short) {
        if( index > -1) {
            set(index, (value and 0xff).toByte())
            set(+index, ((value.toInt() shr 8) and 0xff).toByte())
        } else {
            set(this.cursor++, (value and 0xff).toByte())
            set(this.cursor++, ((value.toInt() shr 8) and 0xff).toByte())
        }
    }

    operator fun set(index: Int = -1, value: Int) {
        if( index > -1) {
            set(index, (value and 0xff).toByte())
            set(+index, ((value shr 8) and 0xff).toByte())
            set(+index, ((value shr 16) and 0xff).toByte())
            set(+index, ((value shr 24) and 0xff).toByte())
        } else {
            set(this.cursor++, (value and 0xff).toByte())
            set(this.cursor++, ((value shr 8) and 0xff).toByte())
            set(this.cursor++, ((value shr 16) and 0xff).toByte())
            set(this.cursor++, ((value shr 24) and 0xff).toByte())
        }
    }

    operator fun set(index: Int = -1, value: Long) {
        if( index > -1) {
            set(index, (value and 0xff).toByte());
            set(+index, ((value shr 8) and 0xff).toByte())
            set(+index, ((value shr 16) and 0xff).toByte())
            set(+index, ((value shr 24) and 0xff).toByte())
            set(+index, ((value shr 32) and 0xff).toByte())
            set(+index, ((value shr 40) and 0xff).toByte())
            set(+index, ((value shr 48) and 0xff).toByte())
            set(+index, ((value shr 54) and 0xff).toByte())
        } else {
            set(this.cursor++, (value and 0xff).toByte());
            set(this.cursor++, ((value shr 8) and 0xff).toByte())
            set(this.cursor++, ((value shr 16) and 0xff).toByte())
            set(this.cursor++, ((value shr 24) and 0xff).toByte())
            set(this.cursor++, ((value shr 32) and 0xff).toByte())
            set(this.cursor++, ((value shr 40) and 0xff).toByte())
            set(this.cursor++, ((value shr 48) and 0xff).toByte())
            set(this.cursor++, ((value shr 54) and 0xff).toByte())
        }
    }

    operator fun set(index: Int = -1, value: ByteArray) {
        if( index > -1) {
            for (i in 0..value.size - 1) {
                set(+index, (value[i].toInt() and 0xff).toByte())
            }
        } else {
            for (i in 0..value.size - 1) {
                set(this.cursor++, (value[i].toInt() and 0xff).toByte())
            }
        }
    }

    operator fun set(index: Int = -1, value: PetroneByteArray) {
        if( index > -1) {
            for (i in 0..value.size - 1) {
                set(index.inc(), (value[i].toInt() and 0xff).toByte())
            }
        } else {
            for (i in 0..value.size - 1) {
                set(this.cursor++, (value[i].toInt() and 0xff).toByte())
            }
        }
    }

    fun toByteArray(): ByteArray {
        return this.array!!
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(this.array!!)
    }

    var size: Int = 0;
    operator fun iterator(): ByteIterator {
        return this.array!!.iterator()
    }

    constructor(size:Int) {
        this.size = size
        array = kotlin.ByteArray(this.size)
    }

    constructor(data:ByteArray) {
        this.size = data.size
        array = data.clone()

    }
}