package com.kkanojia.example.modules

import akka.actor.{ActorSystem, Props}
import com.google.inject.{Inject, Singleton}
import com.kkanojia.example.actors.{TradeAggregateViewActor, UserManager}
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog

import play.api.Logger


@Singleton
class ActorSystemInitializer @Inject()(system: ActorSystem) {

  Logger.info("Initializing Actor system")

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(id))(system)
  endpoint.activate()

  // Initialise event log
  val eventLog = endpoint.logs(ReplicationEndpoint.DefaultLogName)
  // Init User Manager
  val userManagerProps = Props(
    new UserManager(UserManager.ID, Some(UserManager.ID), eventLog)
  )
  system.actorOf(userManagerProps, UserManager.NAME)

  val cumulativeTradeViewProps = Props(
    new TradeAggregateViewActor(TradeAggregateViewActor.ID, eventLog)
  )
  system.actorOf(cumulativeTradeViewProps, TradeAggregateViewActor.NAME)

  Logger.info("Initializing Actor system complete")

}
