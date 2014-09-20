package com.hackwaw

import akka.actor.{ActorRef, Actor}

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClientURI

case class Message()

class QueryDbActor(syncActor: ActorRef) extends Actor {

  val kontomierzApiKey = "oF9Vwpo0QsJhOuu6fwHQuR7GjaRIBC9tHD2Bz15n98KbmZh89CCsPC663eGtVkCz"

  val mongoUri = MongoClientURI("mongodb://kontobit:izKSIIUNS87644@ds063929.mongolab.com:63929/kontobit")

  val mongoClient = MongoClient(mongoUri)

  val col = mongoClient("kontobit")("accounts")

  override def receive: Receive = {
    case Message() =>
      val accountsToUpdate = rowsFromTable()
      if (col.size > 0)
        syncActor ! Feed(accountsToUpdate)
  }

  def rowsFromTable(): Seq[UserAccount] = {
    val allDocs = col.find()
    allDocs.map { doc =>
      UserAccount(
        doc.get("UserId").asInstanceOf[String],
        doc.get("Name").asInstanceOf[String],
        doc.get("Address").asInstanceOf[String],
        kontomierzApiKey,
        591157L,
        doc.get("LastUpdate").asInstanceOf[Long]
      )
    }.toSeq
  }
}

case class UserAccount(userId: String, walletName: String, address: String, apiKey: String, walletId: Long, lastUpdate: Long)

