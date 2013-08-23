package com.thenewmotion.chargenetwork.ocpp.charger

import com.typesafe.scalalogging.slf4j.Logging
import com.thenewmotion.ocpp.chargepoint._
import com.thenewmotion.ocpp.chargepoint.DataTransferReq
import com.thenewmotion.ocpp.DataTransferStatus
import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await
import java.util.concurrent.TimeoutException

/**
 * Implementation of ChargePointService that just logs each method call on it and does nothing else
 */
class ChargePointService(chargerId: String, actor: ActorRef) extends ChargePoint with Logging {

  def clearCache = ClearCacheRes(accepted = false)

  def remoteStartTransaction(req: RemoteStartTransactionReq) = RemoteStartTransactionRes(accepted = false)

  def remoteStopTransaction(req: RemoteStopTransactionReq) = RemoteStopTransactionRes(accepted = false)

  def unlockConnector(req: UnlockConnectorReq) = UnlockConnectorRes(accepted = false)

  def getDiagnostics(req: GetDiagnosticsReq) = GetDiagnosticsRes(None)

  def changeConfiguration(req: ChangeConfigurationReq) = ChangeConfigurationRes(ConfigurationStatus.NotSupported)

  def getConfiguration(req: GetConfigurationReq) = GetConfigurationRes(Nil, req.keys)

  def changeAvailability(req: ChangeAvailabilityReq) = ChangeAvailabilityRes(AvailabilityStatus.Rejected)

  def reset(req: ResetReq) = ResetRes(accepted = false)

  def updateFirmware(req: UpdateFirmwareReq) {}

  def sendLocalList(req: SendLocalListReq) = SendLocalListRes(UpdateStatus.NotSupportedValue)

  def getLocalListVersion = GetLocalListVersionRes(AuthListNotSupported)

  def dataTransfer(req: DataTransferReq) = DataTransferRes(DataTransferStatus.UnknownVendorId)

  def reserveNow(req: ReserveNowReq) = ReserveNowRes(Reservation.Rejected)

  def cancelReservation(req: CancelReservationReq) = CancelReservationRes(accepted = false)

  override def apply[REQ <: Req, RES <: Res](req: REQ)(implicit reqRes: ReqRes[REQ, RES]) = {
    implicit val timeout = Timeout(500 millis)
    val future = actor ? req
    val res = try Await.result(future, timeout.duration).asInstanceOf[RES] catch {
      case _: TimeoutException => super.apply(req)(reqRes)
    }
    logger.info(s"$chargerId\n\t>> $req\n\t<< $res")
    res
  }
}
