package de.choffmeister.sbt

case class VersionString(major: Int, minor: Int, patch: Int, pre: Option[String]) extends Ordered[VersionString] {
  def compare(that: VersionString): Int = {
    if (major < that.major) -1
    else if (major > that.major) 1
    else {
      if (minor < that.minor) -1
      else if (minor > that.minor) 1
      else {
        if (patch < that.patch) -1
        else if (patch > that.patch) 1
        else {
          (pre, that.pre) match {
            case (None, None) => 0
            case (Some(_), None) => -1
            case (None, Some(_)) => 1
            case (Some(pre1), Some(pre2)) => pre1.compare(pre2)
          }
        }
      }
    }
  }

  override def toString(): String = this match {
    case VersionString(major, minor, patch, Some(pre)) => s"$major.$minor.$patch-$pre"
    case _ => s"$major.$minor.$patch"
  }
}

object VersionString {
  def apply(str: String): Option[VersionString] = {
    val regexString = """(\d+)\.(\d+).(\d+)(\-(.+))?"""
    val regex = regexString.r

    regex findFirstIn str match {
      case Some(regex(major, minor, patch, _, pre)) =>
        Option(pre) match {
          case Some(pre) => Some(VersionString(major.toInt, minor.toInt, patch.toInt, Some(pre)))
          case None => Some(VersionString(major.toInt, minor.toInt, patch.toInt, None))
        }
      case _ => None
    }
  }
}
