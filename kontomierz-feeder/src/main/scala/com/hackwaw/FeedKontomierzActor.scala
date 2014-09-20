package com.hackwaw

import java.util.UUID

import akka.actor.Actor
import spray.client.pipelining._
import spray.http._
import spray.httpx.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Feed(updates: Seq[UserAccount])

import com.hackwaw.MyJsonProtocol._


class FeedKontomierzActor extends Actor {


  val walletName = "bitcoin"


  override def receive: Receive = {
    case f: Feed => println("got message")

      f.updates.map(addNewBitcoinTransaction)
  }

  def addNewBitcoinTransaction(ua: UserAccount) {
    val response = queryBitcoinAddress(ua.address)
    response.onFailure {
      case t => println("An error has occured: " + t.getMessage)
    }
    response.map(ar => {
      println("-> final balance: " + ar.final_balance)
      val newTransactions = ar.txs.filter(_.time > ua.lastUpdate)

      for ( t <-newTransactions) {
        val isDeposite = if (ar.address == t.inputs(0).prev_out.addr) false else true
        val amount = Math.abs(t.out(0).value - t.out(1).value)
        postNewTransaction(Transaction(BigDecimal(amount), ua.walletId, uuid = t.hash, isDeposite = isDeposite), ua.apiKey)
      }
    }
    )
  }

  val kontomierzPipeline: HttpRequest => Future[CompleteTx] = (
    addHeader("X-My-Special-Header", "fancy-value")
      ~> sendReceive
      ~> unmarshal[CompleteTx]
    )

  val blockchainPipeline: HttpRequest => Future[AddressResponse] = (
    addHeader("X-My-Special-Header", "fancy-value")
      ~> addCredentials(BasicHttpCredentials("bob", "secret"))
      ~> sendReceive
      ~> unmarshal[AddressResponse]
    )

  def queryBitcoinAddress(address: String): Future[AddressResponse] = {
    blockchainPipeline(Get(s"https://blockchain.info/address/$address?format=json"))
  }

  def postNewTransaction(tx: Transaction, apiKey: String): Future[CompleteTx] = {
    val direction = if (tx.isDeposite) "deposite" else "withdrawal"

    kontomierzPipeline(Post(s"https://kontomierz.pl/k4/money_transactions.json?api_key=$apiKey",
      FormData(Seq("money_transaction[currency_amount]"-> tx.amount.toString(),
        "money_transaction[currency_name]"-> tx.currency,
        "money_transaction[direction]" -> direction,
        "money_transaction[client_assigned_id]" -> tx.uuid,
        "money_transaction[user_account_id]" -> tx.walletId.toString
      ))))
  }
}

case class Transaction(amount: BigDecimal, walletId: Long, currency: String = "BTC", uuid: String = UUID.randomUUID().toString, isDeposite: Boolean)


