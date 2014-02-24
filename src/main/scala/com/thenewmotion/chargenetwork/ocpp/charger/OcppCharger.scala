package com.thenewmotion.chargenetwork.ocpp.charger

import java.net.URI
import dispatch.Http
import akka.actor.Props
import com.thenewmotion.ocpp.{CentralSystemClient, Version}

class OcppCharger(chargerId: String,
                  numConnectors: Int,
                  ocppVersion: Version.Value,
                  centralSystemURL: URI,
                  server: ChargerServer,
                  http: Http = new Http) {

  val client = CentralSystemClient(chargerId, ocppVersion, centralSystemURL, http, Some(server.url))
  val chargerActor = system.actorOf(Props(new ChargerActor(BosService(chargerId, client), numConnectors)))
  server.actor ! ChargerServer.Register(chargerId, new ChargePointService(chargerId, chargerActor))
}

