package kr.co.byrobot.petroneapi.BLE

import android.bluetooth.BluetoothDevice
import android.os.Message
import java.util.*

/**
 * Created by byrobot on 2017. 9. 13..
 */

class BLEServiceHandle : AppHandler<BLEService> {
    constructor(reference: BLEService ) : super(reference) {
        mMessageListener = object: HandleMessageListener<BLEService> {
            override fun onHandleMessage(reference: BLEService, msg: Message) {
                bleServiceMessage(reference, msg);
            }
        }
    }

    fun bleServiceMessage(reference: BLEService, msg: Message) {
        if( msg != null && reference != null ) {
            val data = msg.data

            when( msg.what ) {
                BLEConstants.MSG_CONTROL_ID_REGISTER.code -> {
                    if( msg.replyTo != null ) {
                        reference.addClient( msg.replyTo )
                    }
                }
                BLEConstants.MSG_CONTROL_ID_UNREGISTER.code -> {
                    if( msg.replyTo != null ) {
                        reference.removeClient( msg.replyTo )
                    }
                }
                BLEConstants.MSG_CONTROL_ID_START_SCAN.code -> {
                    if( msg.replyTo != null ) {
                        reference.setScanCallback( msg.replyTo )
                    }
                    reference.startScan()
                }
                BLEConstants.MSG_CONTROL_ID_STOP_SCAN.code -> {
                    reference.stopScan()
                }
                BLEConstants.MSG_CONTROL_ID_CONNECT_MAC.code -> {
                    val mac : String = msg.obj as String
                    if( mac != null ) {
                        reference.connectDevice(mac)
                    }
                }
                BLEConstants.MSG_CONTROL_ID_CONNECT_DEVICE.code -> {
                    val device : BluetoothDevice = data.getParcelable<BluetoothDevice>("bleDevice")
                    if( device != null ) {
                        reference.connectDevice(device)
                    }
                }
                BLEConstants.MSG_CONTROL_ID_READ_CHARACTERISTIC.code -> {
                    reference.readFromCharacteristic(
                            UUID.fromString("C320DF00-7891-11E5-8BCF-FEFF819CDC9F"),
                            UUID.fromString("C320DF01-7891-11E5-8BCF-FEFF819CDC9F"))
                }
                BLEConstants.MSG_CONTROL_ID_WRITE_CHARACTERISTIC.code -> {
                    val values = msg.obj as ByteArray

                    reference.writeToCharacteristic(
                            UUID.fromString("C320DF00-7891-11E5-8BCF-FEFF819CDC9F"),
                            UUID.fromString("C320DF02-7891-11E5-8BCF-FEFF819CDC9F"),
                            values
                            )
                }
                BLEConstants.MSG_CONTROL_ID_DESCRIPTOR_NOTIFICATION.code -> {
                    reference.updateCharacteristicNotification(
                            UUID.fromString("C320DF00-7891-11E5-8BCF-FEFF819CDC9F"),
                            UUID.fromString("C320DF01-7891-11E5-8BCF-FEFF819CDC9F"),
                            UUID.fromString("C320DF02-7891-11E5-8BCF-FEFF819CDC9F"), true)
                }
            }
        }
    }
}