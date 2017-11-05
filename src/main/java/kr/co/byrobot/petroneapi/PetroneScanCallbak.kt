package kr.co.byrobot.petroneapi

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneScanCallbak {
    fun onScan(callback: ((String)->Unit) );
    fun onScanFailure(callback: ((Error)->Unit));
}