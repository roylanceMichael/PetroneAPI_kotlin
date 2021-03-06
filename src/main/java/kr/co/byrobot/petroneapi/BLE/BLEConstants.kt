package kr.co.byrobot.petroneapi.BLE

/**
 * Created by byrobot on 2017. 9. 13..
 */
enum class BLEConstants(val code: Int) {
  //ble response message id
  MSG_BLE_ID_CHARACTERISTIC_WRITE(20000),
  MSG_BLE_ID_DESCRIPTOR_WRITE(20001),
  MSG_BLE_ID_CHARACTERISTIC_NOTIFICATION(20002),
  MSG_BLE_ID_CHARACTERISTIC_READ(20003),
  MSG_BLE_ID_DESCRIPTOR_READ(20004),
  MSG_BLE_ID_RELIABLE_WRITE_COMPLETED(20005),
  MSG_BLE_ID_READ_REMOTE_RSSI(20006),
  MSG_BLE_ID_MTU_CHANGED(20007),
  MSG_BLE_ID_SERVICES_DISCOVERED(20008),


  //ble control message id
  MSG_CONTROL_ID_REGISTER(30000),
  MSG_CONTROL_ID_UNREGISTER(30001),
  MSG_CONTROL_ID_CONNECT_DEVICE(30002),
  MSG_CONTROL_ID_CONNECT_MAC(30003),
  MSG_CONTROL_ID_START_SCAN(30004),
  MSG_CONTROL_ID_STOP_SCAN(30005),
  MSG_CONTROL_ID_FIND_DEVICE(30006),
  MSG_CONTROL_ID_SCAN_STOPED(30007),


  //ble read message id
  MSG_CONTROL_ID_WRITE_CHARACTERISTIC(40000),
  MSG_CONTROL_ID_DESCRIPTOR_NOTIFICATION(40001),
  MSG_CONTROL_ID_READ_CHARACTERISTIC(40002),


  BLE_MSG_ID_CONNECTION_STATE_CHANGED(10000);


  val BLE_MSG_SERVICE_UUID_KEY = "C320DF00-7891-11E5-8BCF-FEFF819CDC9F"
  val BLE_MSG_DESCRIPTOR_UUID_KEY = "C320DF01-7891-11E5-8BCF-FEFF819CDC9F"
  val BLE_MSG_CHARACTERISTIC_UUID_KEY = "C320DF02-7891-11E5-8BCF-FEFF819CDC9F"
}