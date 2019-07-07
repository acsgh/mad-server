package com.acsgh.scala.mad.router.http.handler

import com.acsgh.scala.mad.router.http.RequestContext
import com.acsgh.scala.mad.router.http.exception.BadRequestException
import com.acsgh.scala.mad.router.http.model.{Response, ResponseStatus}

object DefaultExceptionHandler {

  def stacktraceToHtml(throwable: Throwable): String = {
    var result = "<p>"
    result += stacktraceToHtmlInternal(throwable, causeThrowable = false)
    var cause = throwable.getCause

    while (cause != null) {
      result += stacktraceToHtmlInternal(cause, causeThrowable = true)
      cause = cause.getCause
    }

    result += "</p>"
    result
  }

  private def stacktraceToHtmlInternal(throwable: Throwable, causeThrowable: Boolean) = {
    var result = ""
    result += "<b>"
    if (causeThrowable) {
      result += "Caused by: "
    }
    result += throwable.getClass.getName + ":&nbsp;" + "</b>" + throwable.getMessage + "<br/>\n"
    for (stackTraceElement <- throwable.getStackTrace) {
      result += "&nbsp;&nbsp;&nbsp;&nbsp;" + stackTraceElement + "<br/>\n"
    }
    result
  }
}

class DefaultExceptionHandler(productionMode: => Boolean) extends ExceptionHandler {
  override def handle(throwable: Throwable)(implicit requestContext: RequestContext): Response = {
    val status = if (throwable.isInstanceOf[BadRequestException]) ResponseStatus.BAD_REQUEST else ResponseStatus.INTERNAL_SERVER_ERROR
    responseStatus(status) {
      responseBody(getStatusBody(status, throwable))
    }
  }

  private def getStatusBody(status: ResponseStatus, throwable: Throwable): String = {
    s"""<html>
       |<head>
       |   <title>${status.code} - ${status.message}</title>
       |</head>
       |<body>
       |   <h2>${status.code} - ${status.message}</h2>
       |   ${if (productionMode) "" else DefaultExceptionHandler.stacktraceToHtml(throwable)}
       |</body>
       |</html>""".stripMargin
  }
}
