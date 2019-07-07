package com.acsgh.scala.mad.router.ws.directives

import com.acsgh.scala.mad.router.ws.WSRequestContext
import com.acsgh.scala.mad.router.ws.convertions.DefaultFormats
import com.acsgh.scala.mad.router.ws.model.{WSRequestBinary, WSRequestText, WSResponse}

trait Directives extends DefaultFormats {
  def requestString(action: String => Option[WSResponse])(implicit context: WSRequestContext): Option[WSResponse] = context.request match {
    case t: WSRequestText => action(t.text)
    case _ => None
  }

  def requestBytes(action: Array[Byte] => Option[WSResponse])(implicit context: WSRequestContext): Option[WSResponse] = context.request match {
    case t: WSRequestBinary => action(t.bytes)
    case _ => None
  }

  def responseBody(input: Array[Byte])(implicit context: WSRequestContext): Option[WSResponse] = Some(context.response.bytes(input))

  def responseBody(input: String)(implicit context: WSRequestContext): Option[WSResponse] = Some(context.response.text(input))
}
