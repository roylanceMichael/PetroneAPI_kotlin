package kr.co.byrobot.petroneapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import kotlinx.coroutines.experimental.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetroneCRC
import kr.co.byrobot.petroneapi.Packet.PetronePacket
import kr.co.byrobot.petroneapi.Wifi.*
import java.net.InetSocketAddress
import java.nio.*
import java.util.*

/**
 * Created by byrobot on 2017. 6. 28..
 */
class PetroneWIFI : PetroneNetworkInterface {
    var context:Context?= null
    var wifi : WifiManager?= null
    var receiver:BroadcastReceiver?= null
    var connected: Boolean = false
    var interupted: Boolean = true
    var packetList: MutableList<PetronePacket>?= null
    var connectList: MutableList<String>?= null

    init {
        packetList = mutableListOf<PetronePacket>()
        connectList = mutableListOf<String>()
    }

    constructor(context:Context) {
        this.context = context
    }

    override fun initWithCallback(callback: PetroneCallback) {
        context?.registerReceiver(receiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
    }

    override fun scan(onScan: (ssid:String, name:String, rssi:Int) -> Unit, onScanFailure : (error:String) -> Unit) {
        wifi = this.context!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info : WifiInfo = wifi!!.connectionInfo

        for( config:WifiConfiguration in wifi!!.configuredNetworks ) {
            if( config == null ) {
                onScanFailure( "AP ERROR" )
            } else {
            }
        }

        if( receiver != null ) {
            context?.unregisterReceiver(receiver)
            receiver = null
        }

        receiver = object : BroadcastReceiver() {
            public override fun onReceive(context: Context, intent: Intent?) {
                val action:String = intent!!.action

                if( action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) ) {
                    val results = wifi!!.scanResults

                    for (result: ScanResult in results) {
                        if (result.level < 0 && result.SSID.contains(Regex("Petrone FPV"))) {
                            onScan( result.BSSID, result.SSID, result.level );
                        }
                    }
                }
            }
        }

        context?.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifi!!.startScan()
    }

    override fun stopScan() {
        context?.unregisterReceiver(receiver)
        receiver = null
    }

    private var connectionCallback : PetroneConnectionCallback? = null
    override fun connect(target: String, callback: PetroneConnectionCallback)  {
        // 1st. Find manager
        var wifi : WifiManager = this.context!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info : WifiInfo = wifi.connectionInfo

        if( callback != null ) {
            connectionCallback = callback
        }

        // 2nd. If target ap found. Connect to ap.
        for( config:WifiConfiguration in wifi.configuredNetworks ) {
            if( config != null ) {
                if ( config.BSSID.compareTo(target) == 0 ) {
                    wifi.disconnect() // Disconnect from prev connection

                    if( wifi.enableNetwork(config.networkId, true) ) {
                        if( connectList != null ) connectList?.clear()
                        else connectList = mutableListOf<String>()

                        connectToPetrone()
                    } else {
                        if( wifi.reconnect() ) { // Try once again.
                            if( connectList != null ) connectList?.clear()
                            else connectList = mutableListOf<String>()

                            connectToPetrone()
                        } else {
                            connectionCallback!!.onError("WIFI AP has something error.")
                        }
                    }
                    break
                }
            }
        }

        // Last. Get WIFI STATUS CHANGE to connected. Try connect to WIFI module.
    }

    override fun isConnected() : Boolean {
        if( connectList == null ) return false
        return !connectList!!.isEmpty()
    }

    override fun getConnectionList(): List<String> {
        if( connected ) {
            return connectList!!
        } else {
            return mutableListOf<String>()
        }
    }

    override fun disconnect(target: String) {
        interupted = true
    }

    override fun sendPacket(packet: PetronePacket) {
        if( packet != null ) {
            packetList!!.add(packet)
        }
    }

    override fun recvPacket(data: ByteArray) {
        val bufferSize = (data.get(3).toInt() and 0xff) + 1
        var recvData : ByteArray = kotlin.ByteArray(bufferSize)

        recvData[0] = data[2]
        for (i in 1..bufferSize -1) {
            recvData[i] = data[3+i]
        }

        connectionCallback!!.onRecvPacket(recvData)
    }

    private fun connectToPetrone() : Boolean {
        runBlocking () {
            Thread.sleep(5000)
            petroneClientImpl( 23000 )
        }
        return true
    }

    private fun getSendPacket() : ByteBuffer {
        if( packetList!!.isEmpty() != true ) {
            val packet: PetronePacket = packetList!!.get(0)

            if (packet != null) {
                var data: PetroneByteArray = PetroneByteArray(packet.size + 6);
                data.set(value = 0x0a)
                data.set(value = 0x55)
                data.set(value = packet.encodeSerial())
                data.set(value = PetroneCRC().getCRC(packet.encodeSerial().toByteArray()))

                return data.toByteBuffer()
            }
        }

        return ByteBuffer.allocate(0)
    }

    suspend fun petroneClientImpl(port: Int) {
        petroneSocket()
                .tcp()
                .connect(InetSocketAddress("192.168.100.1", port))
                .use {
                    socket ->
                    connectList!!.add("FPVKit")

                    val byteBuffer : ByteBuffer = ByteBuffer.allocate(8192)

                    while (!interupted && !Thread.interrupted() ) {
                        byteBuffer.clear()
                        val rc = socket.read(byteBuffer)
                        byteBuffer.flip()

                        if( rc > 0 ) {
                            recvPacket( byteBuffer.array() )
                        } else if( rc < 0 ) {
                            interupted = false
                        }

                        socket.write(getSendPacket())
                    }

                    socket.close()
                    connected = false
                }
    }
}