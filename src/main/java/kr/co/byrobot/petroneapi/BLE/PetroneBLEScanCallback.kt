package kr.co.byrobot.petroneapi.BLE

import android.bluetooth.BluetoothDevice



/**
 * Created by byrobot on 2017. 7. 18..
 */
interface PetroneBLEScanCallback {
    fun onBleScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray)
    fun onBleScanFailed(scanState: BLEScanState)
}