package acsgh.mad.scala.server.router.http.convertions

import acsgh.mad.scala.server.router.http.directives._
import acsgh.mad.scala.server.router.http.model.HttpRequestContext

import scala.language.implicitConversions

trait HttpDefaultParamHandling {

  implicit object StringWriter extends HttpParamWriter[String] {
    override def write(input: String): String = input
  }

  implicit object StringReader extends HttpParamReader[String] {
    override def read(input: String): String = input
  }

  implicit object LongWriter extends HttpParamWriter[Long] {
    override def write(input: Long): String = input.toString
  }

  implicit object LongReader extends HttpParamReader[Long] {
    override def read(input: String): Long = input.toLong
  }

  implicit def string2Param(name: String)(implicit reader: HttpParamReader[String]): Param[String, String] = SingleParam[String](name)

  implicit class StringParamsEnhanced(name: String) {
    def as[T](implicit reader: HttpParamReader[T]): SingleParam[T] = SingleParam[T](name)

    def opt: OptionParam[String] = OptionParam[String](name)

    def default(defaultValue: String): DefaultParam[String] = DefaultParam[String](name, defaultValue)

    def list: ListParam[String] = ListParam[String](name)
  }

  implicit class SingleParamEnhanced[P](param: SingleParam[P])(implicit reader: HttpParamReader[P]) {
    def opt: OptionParam[P] = OptionParam[P](param.name)

    def default(defaultValue: P): DefaultParam[P] = DefaultParam[P](param.name, defaultValue)

    def list: ListParam[P] = ListParam[P](param.name)
  }

  implicit class ParamsEnhanced[P, R](param: Param[P, R]) {
    def multipartValue(implicit context: HttpRequestContext, bodyContent: Multipart): R = {
      val value = bodyContent.parts.find(_.name.equalsIgnoreCase(param.name)).map(part => List(part.content)).getOrElse(List())
      param("Multipart", value)
    }
    def formValue(implicit context: HttpRequestContext, bodyContent: UrlFormEncodedBody): R = {
      val value = bodyContent.params.find(_._1.equalsIgnoreCase(param.name)).map(_._2).getOrElse(List())
      param("Form", value)
    }

    def queryValue(implicit context: HttpRequestContext): R = {
      val value = context.request.queryParams.find(_._1.equalsIgnoreCase(param.name)).map(_._2).getOrElse(List())
      param("Query", value)
    }

    def pathValue(implicit context: HttpRequestContext): R = {
      val value = context.pathParams.find(_._1.equalsIgnoreCase(param.name)).map(_._2).toList
      param("Path", value)
    }

    def cookieValue(implicit context: HttpRequestContext): R = {
      val value = context.request.cookieParams.find(_._1.equalsIgnoreCase(param.name)).map(_._2).getOrElse(List())
      param("Cookie", value)
    }

    def headerValue(implicit context: HttpRequestContext): R = {
      val value = context.request.headers.find(_._1.equalsIgnoreCase(param.name)).map(_._2).getOrElse(List())
      param("Header", value)
    }
  }

}
