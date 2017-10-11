package kr.co.byrobot.petroneapi

import kr.co.byrobot.petroneapi.Data.*

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneCallback {
    /*!
     *  @method disconnected:
     *
     *  @param reason             disconnected reason
     *
     *  @discussion            This method is invoked when the disconnected frome PETRONE
     */
    fun disconnected(reason:String )
    /*!
     *  @method connectionComplete:
     *
     *  @param petroneController    The send petroneController
     *  @param response             Response PetroneDataType
     *
     *  @discussion            This method is invoked when the connect to PETRONE
     */
    fun connectionComplete(complete:String )
    /*!
     *  @method recvFromPetroneResponse:
     *
     *  @param petroneController    The send petroneController
     *  @param response             Response PetroneDataType
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Ack response.
     */
    fun recvFromPetroneResponse(response:Byte )
    /*!
     *  @method recvFromPetroneStatus:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType status.
     */
    fun recvFromPetroneResponse(status:PetroneStatus )
    /*!
     *  @method recvFromPetroneTrim:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType TrimFlight & TrimDrive Information.
     */
    fun recvFromPetroneResponse(trim:PetroneTrim )
    /*!
     *  @method recvFromPetroneTrimFlight:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType TrimFlight information.
     */
    fun recvFromPetroneResponse(trimFlight:PetroneTrimFlight )
    /*!
     *  @method recvFromPetroneTrimDrive:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType TrimDrive information.
     */
    fun recvFromPetroneResponse(trimDrive:PetroneTrimDrive )
    /*!
     *  @method recvFromPetroneAttitude:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Attitude information.
     */
    fun recvFromPetroneResponse(attitude:PetroneAttitude )
    /*!
     *  @method recvFromPetroneGyroBias:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType GyroBias information.
     */
    fun recvFromPetroneResponse(gyroBias:PetroneGyroBias )
    /*!
     *  @method recvFromPetroneFlightCount:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType CountFlight information.
     */
    fun recvFromPetroneResponse(flightCount:PetroneCountFlight )
    /*!
     *  @method recvFromPetroneDriveCount:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType CountDrive information.
     */
    fun recvFromPetroneResponse(driveCount:PetroneCountDrive )
    /*!
     *  @method recvFromPetroneImuRawAndAngle:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType ImuRawAndAngle information.
     */
    fun recvFromPetroneResponse(motor:PetroneImuRawAndAngle )
    /*!
     *  @method recvFromPetronePressure:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Pressure information.
     */
    fun recvFromPetroneResponse(motor:PetronePressure )
    /*!
     *  @method recvFromPetroneImageFlow:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Motor information.
     */
    fun recvFromPetroneResponse(motor:PetroneImageFlow )
    /*!
     *  @method recvFromPetroneMotor:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Motor information.
     */
    fun recvFromPetroneResponse(motor:PetroneMotor )
    /*!
     *  @method recvFromPetroneTemperature:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType TEmperature information.
     */
    fun recvFromPetroneResponse(temperature:PetroneTemperature )
    /*!
     *  @method recvFromPetroneRange:
     *
     *  @param petroneController    The send petroneController
     *
     *  @discussion            This method is invoked when the @recv of PetroneDataType Range information.
     */
    fun recvFromPetroneResponse(range:PetroneRange )
}