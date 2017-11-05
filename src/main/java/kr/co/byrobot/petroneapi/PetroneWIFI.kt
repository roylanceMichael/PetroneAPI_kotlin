package kr.co.byrobot.petroneapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import kotlinx.coroutines.experimental.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetroneCRC
import kr.co.byrobot.petroneapi.Packet.PetronePacket
import kr.co.byrobot.petroneapi.Wifi.*
import java.net.InetSocketAddress
import java.nio.*

/**
 * Created by byrobot on 2017. 6. 28..
 */
class PetroneWIFI(context: Context) : PetroneNetworkInterface {
  var context: Context? = context
  var wifi: WifiManager? = null
  var receiver: BroadcastReceiver? = null
  var connected: Boolean = false
  var interupted: Boolean = true
  var packetList: MutableList<PetronePacket>? = null
  var connectList: MutableList<String>? = null

  init {
    packetList = mutableListOf<PetronePacket>()
    connectList = mutableListOf<String>()
  }

  override fun initWithCallback(callback: PetroneCallback) {
    context?.registerReceiver(receiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
  }

  override fun scan(onScan: (ssid: String, name: String, rssi: Int) -> Unit,
      onScanFailure: (error: String) -> Unit) {
    wifi = this.context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

    wifi?.configuredNetworks?.forEach {
      if (it == null) {
        onScanFailure("AP ERROR")
      }
    }

    if (receiver != null) {
      context?.unregisterReceiver(receiver)
      receiver = null
    }

    receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
          if (it.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            wifi?.scanResults?.let { results ->
              results.forEach { result ->
                if (result.level < 0 && result.SSID.contains(Regex("Petrone FPV"))) {
                  onScan(result.BSSID, result.SSID, result.level);
                }
              }
            }
          }
        }
      }
    }

    context?.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    wifi?.startScan()
  }

  override fun stopScan() {
    context?.unregisterReceiver(receiver)
    receiver = null
  }

  private var connectionCallback: PetroneConnectionCallback? = null
  override fun connect(target: String, callback: PetroneConnectionCallback) {
    // 1st. Find manager
    val wifi: WifiManager? = this.context?.applicationContext?.getSystemService(
        Context.WIFI_SERVICE) as WifiManager?

    connectionCallback = callback

    // 2nd. If target ap found. Connect to ap.
    wifi?.configuredNetworks?.forEach { config ->
      if (config != null) {
        if (config.BSSID.compareTo(target) == 0) {
          wifi.disconnect() // Disconnect from prev connection

          if (wifi.enableNetwork(config.networkId, true)) {
            if (connectList != null) connectList?.clear()
            else connectList = mutableListOf<String>()

            connectToPetrone()
          } else {
            if (wifi.reconnect()) { // Try once again.
              if (connectList != null) connectList?.clear()
              else connectList = mutableListOf<String>()

              connectToPetrone()
            } else {
              connectionCallback?.onError("WIFI AP has something error.")
            }
          }
          return@forEach
        }
      }
    }
    // Last. Get WIFI STATUS CHANGE to connected. Try connect to WIFI module.
  }

  override fun isConnected(): Boolean {
    return connectList?.isEmpty() ?: return false
  }

  override fun getConnectionList(): List<String> {
    val tempConnectList = connectList
    if (connected && tempConnectList != null) {
      return tempConnectList
    } else {
      return listOf()
    }
  }

  override fun disconnect(target: String) {
    interupted = true
  }

  override fun sendPacket(packet: PetronePacket) {
    packetList?.add(packet)
  }

  override fun recvPacket(data: ByteArray) {
    val bufferSize = (data.get(3).toInt() and 0xff) + 1
    val recvData: ByteArray = kotlin.ByteArray(bufferSize)

    recvData[0] = data[2]
    for (i in 1 until bufferSize) {
      recvData[i] = data[3 + i]
    }

    connectionCallback?.onRecvPacket(recvData)
  }

  private fun connectToPetrone(): Boolean {
    runBlocking {
      Thread.sleep(5000)
      petroneClientImpl(23000)
    }
    return true
  }

  private fun getSendPacket(): ByteBuffer {
    if (packetList?.isEmpty() != true) {
      val packet: PetronePacket? = packetList?.get(0)

      if (packet != null) {
        val data = PetroneByteArray(packet.size + 6);
        data.set(value = 0x0a)
        data.set(value = 0x55)
        data.set(value = packet.encodeSerial())
        data.set(value = PetroneCRC().getCRC(packet.encodeSerial().toByteArray()))

        return data.toByteBuffer()
      }
    }

    return ByteBuffer.allocate(0)
  }

  private suspend fun petroneClientImpl(port: Int) {
    petroneSocket()
        .tcp()
        // todo: hard coded?
        .connect(InetSocketAddress("192.168.100.1", port))
        .use { socket ->
          connectList?.add("FPVKit")

          val byteBuffer: ByteBuffer = ByteBuffer.allocate(8192)

          while (!interupted && !Thread.interrupted()) {
            byteBuffer.clear()
            val rc = socket.read(byteBuffer)
            byteBuffer.flip()

            if (rc > 0) {
              recvPacket(byteBuffer.array())
            } else if (rc < 0) {
              interupted = false
            }

            socket.write(getSendPacket())
          }

          socket.close()
          connected = false
        }
  }
}