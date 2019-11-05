package acsgh.mad.scala.examples.netty

import acsgh.mad.scala.router.http.listener.LoggingEventListener
import acsgh.mad.scala.router.ws.listener.WSLoggingEventListener
import acsgh.mad.scala.{Server, ServerApp, ServerBuilder}

object Boot extends ServerApp {

  override val name: String = "Netty Boot Example"

  override protected def buildServer(builder: ServerBuilder): Server = {
    builder.http.addRequestListeners(LoggingEventListener)
    builder.ws.addRequestListeners(WSLoggingEventListener)

    builder.http.resourceFolder("/", "public")
    builder.http.webjars()
    PersonRoutes(builder)

    builder.build()
  }
}
