package kr.co.byrobot.petroneapi.BLE

import android.bluetooth.*

/**
 * Created by byrobot on 2017. 7. 17..
 */
object PetroneGattCallback : BluetoothGattCallback(){
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                         newState: Int) {
        if( status == BluetoothGatt.GATT_SUCCESS ) {

        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {}

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic,
                             status: Int) {
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt,
                              characteristic: BluetoothGattCharacteristic, status: Int) {
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                characteristic: BluetoothGattCharacteristic) {
    }

    override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor,
                         status: Int) {
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor,
                          status: Int) {
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {}

    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {}

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {}
}