package acsgh.mad.scala.server.router.http.directives

import acsgh.mad.scala.core.http.exception.BadRequestException
import acsgh.mad.scala.server.router.http.convertions.HttpParamReader

trait Param[P, R] {

  val name: String

  def apply(paramType: String, input: List[String]): R
}

case class SingleParam[P](name: String)(implicit reader: HttpParamReader[P]) extends Param[P, P] {
  override def apply(paramType: String, input: List[String]): P = {
    try {
      input.headOption.map(reader.read) match {
        case Some(v) => v
        case _ => throw new BadRequestException(s"""Missing mandatory ${paramType.toLowerCase} param: "$name"""");
      }

    } catch {
      case e: BadRequestException => throw e;
      case e: Throwable => throw new BadRequestException(s"""Invalid ${paramType.toLowerCase} param: "$name"""", e);
    }
  }
}

case class DefaultParam[P](name: String, defaultValue: P)(implicit reader: HttpParamReader[P]) extends Param[P, P] {
  override def apply(paramType: String, input: List[String]): P = {
    try {
      input.headOption.map(reader.read) match {
        case Some(v) => v
        case _ => defaultValue
      }

    } catch {
      case e: BadRequestException => throw e;
      case e: Throwable => throw new BadRequestException(s"""Invalid ${paramType.toLowerCase} param: "$name"""", e);
    }
  }
}

case class OptionParam[P](name: String)(implicit reader: HttpParamReader[P]) extends Param[P, Option[P]] {
  override def apply(paramType: String, input: List[String]): Option[P] = {
    try {
      input.headOption.map(reader.read)
    } catch {
      case e: BadRequestException => throw e;
      case e: Throwable => throw new BadRequestException(s"""Invalid ${paramType.toLowerCase} param: "$name"""", e);
    }
  }
}

case class ListParam[P](name: String)(implicit reader: HttpParamReader[P]) extends Param[P, List[P]] {
  override def apply(paramType: String, input: List[String]): List[P] = {
    try {
      input.map(reader.read)
    } catch {
      case e: BadRequestException => throw e;
      case e: Throwable => throw new BadRequestException(s"""Invalid ${paramType.toLowerCase} param: "$name"""", e);
    }
  }
}