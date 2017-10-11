package kr.co.byrobot.petroneapi.BLE

import android.bluetooth.BluetoothGatt
import java.util.*

/**
 * Created by byrobot on 2017. 9. 18..
 */


interface PetroneBLECallback {

    fun onFailed( uuid: UUID ,msg: String) { }
    fun onFailed(msg: String ) { }


    //Callback triggered as a result of a remote characteristic notification.
    //BluetoothGattCallback#onCharacteristicChanged --> onCharacteristicNotification
    fun onCharacteristicNotification(uuid : UUID, data: ByteArray ) { }

    //Callback reporting the result of a characteristic read operation.
    //BluetoothGattCallback#onCharacteristicChanged
    fun onCharacteristicRead(uuid: UUID, data: ByteArray ) { }

    //Callback indicating the result of a characteristic write operation.
    //BluetoothGattCallback#onCharacteristicWrite
    fun onCharacteristicWrite(uuid: UUID, status: Int) { }

    //Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
    //BluetoothGattCallback#onConnectionStateChange
    fun onConnectionStateChange(status: Int, newStatus: Int) { }

    //Callback reporting the result of a descriptor read operation.
    //BluetoothGattCallback#onDescriptorRead
    fun onDescriptorRead(uuid: UUID, data: ByteArray) { }

    //Callback indicating the result of a descriptor write operation.
    //BluetoothGattCallback#onDescriptorWrite
    fun onDescriptorWrite(uuid: UUID, status: Int)
    {

    }

    //Callback indicating the MTU for a given device connection has changed.
    //BluetoothGattCallback#onConnectionStateChange
    fun onMtuChanged(mtu: Int, status: Int)
    {

    }

    //Callback reporting the RSSI for a remote device connection.
    //BluetoothGattCallback#onReadRemoteRssi
    fun onReadRemoteRssi(rssi: Int, status: Int)
    {

    }

    //Callback invoked when a reliable write transaction has been completed.
    //BluetoothGattCallback#onReliableWriteCompleted
    fun onReliableWriteCompleted(status: Int)
    {

    }

    //Callback invoked when the list of remote services,
    // characteristics and descriptors for the remote device have been updated,
    // ie new services have been discovered.
    //BluetoothGattCallback#onServicesDiscovered
    fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int)
    {

    }
}