package com.hackwaw

import spray.json.DefaultJsonProtocol

case class Order(id: Int)

case class AddressResponse(hash160: String,
                           address: String,
                           n_tx: BigDecimal,
                           total_received: BigDecimal,
                           total_sent: BigDecimal,
                           final_balance: BigDecimal,
                           txs: List[Tx])

case class Tx(
               result: Int,
               time: Long,
               inputs: List[In],
               vout_sz: Int,
               relayed_by: String,
               hash: String,
               vin_sz: Int,
               tx_index: Int,
               ver: Int,
               out: List[Out],
               size: Int
               )

case class In(
               sequence: Long,
               prev_out: Out,
               script: String
               )

case class Out(n: Int,
               value: Int,
               addr: String,
               tx_index: Int,
               spent: Option[Boolean],
               `type`: Int,
               script: String)


case class CompleteTx (money_transaction: MoneyTransaction)
case class MoneyTransaction(id: Long, user_account_id: Long, uid: String, amount: String, transaction_on: String, booked_on: String,
                            title: String, category_id: Long, currency_amount: String, plain_party_iban: String, description: String, category_name: String,
                            currency_name: String, tag_string: String)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val orderFormat = jsonFormat1(Order)
  implicit val outFormat = jsonFormat7(Out)
  implicit val inFormat = jsonFormat3(In)
  implicit val txFormat = jsonFormat11(Tx)
  implicit val addressResponseFormat = jsonFormat7(AddressResponse)

  implicit val moneyTransactionFormat = jsonFormat14(MoneyTransaction)
  implicit val completeTx = jsonFormat1(CompleteTx)
}
