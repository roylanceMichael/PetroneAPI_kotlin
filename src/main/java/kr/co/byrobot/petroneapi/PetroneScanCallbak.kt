package kr.co.byrobot.petroneapi

import android.bluetooth.BluetoothDevice
import android.net.wifi.WifiConfiguration

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneScanCallbak {
    fun onScan(callback: ((String)->Unit) );
    fun onScanFailure(callback: ((Error)->Unit));
}