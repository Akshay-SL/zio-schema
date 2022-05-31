package zio.schema.validation

import java.time.format.DateTimeFormatter

import scala.util.Try

import zio.test._

object ValidationSpec extends DefaultRunnableSpec {
  import zio.schema.validation.ValidationSpec.Hour._
  import zio.schema.validation.ValidationSpec.Minute._
  import zio.schema.validation.ValidationSpec.Second._
  import zio.schema.validation.ValidationSpec.Fraction._
  import zio.schema.validation.ValidationSpec.AmPm._

  def spec: ZSpec[Environment, Failure] = suite("ValidationSpec")(
    test("Greater than") {
      val validation = Validation.greaterThan(4)

      assertTrue(validation.validate(4).isLeft) &&
      assertTrue(validation.validate(5).isRight) &&
      assertTrue(validation.validate(6).isRight) &&
      assertTrue(validation.validate(3).isLeft)
    },
    test("Less than") {
      val validation = Validation.lessThan(4)

      assertTrue(validation.validate(3).isRight) &&
      assertTrue(validation.validate(2).isRight) &&
      assertTrue(validation.validate(5).isLeft) &&
      assertTrue(validation.validate(4).isLeft)
    },
    test("Equal to") {
      val validation = Validation.equalTo(3)

      assertTrue(validation.validate(3).isRight) &&
      assertTrue(validation.validate(5).isLeft) &&
      assertTrue(validation.validate(2).isLeft)
    },
    test("MinLength") {
      val validation = Validation.minLength(4)

      assertTrue(validation.validate("hello").isRight) &&
      assertTrue(validation.validate("Todd").isRight) &&
      assertTrue(validation.validate("how").isLeft)
      assertTrue(validation.validate("hi").isLeft)
    },
    test("MaxLength") {
      val validation = Validation.maxLength(4)

      assertTrue(validation.validate("Todd").isRight) &&
      assertTrue(validation.validate("how").isRight) &&
      assertTrue(validation.validate("hello").isLeft) &&
      assertTrue(validation.validate("Automobile").isLeft)
    },
    test("Regex digit or letter Validation") {
      val validation = Validation.regex(Regex.digitOrLetter)

      assertTrue(validation.validate("a").isRight) &&
      assertTrue(validation.validate("1").isRight) &&
      assertTrue(validation.validate("12").isLeft) &&
      assertTrue(validation.validate("*").isLeft) &&
      assertTrue(validation.validate("ab").isLeft) &&
      assertTrue(validation.validate("").isLeft) &&
      assertTrue(validation.validate("&").isLeft)
    },
    test("Regex identifier Validation") {
      val validation = Validation.identifier

      assertTrue(validation.validate("_").isRight) &&
      assertTrue(validation.validate("a").isRight) &&
      assertTrue(validation.validate("ab").isRight) &&
      assertTrue(validation.validate("").isLeft) &&
      assertTrue(validation.validate("*").isLeft)
    },
    test("Regex email Validation") {
      val validation = Validation.email

      assertTrue(validation.validate("bob101@gmail.com").isRight) &&
      assertTrue(validation.validate("bob_and_alice@gmail.com").isRight) &&
      assertTrue(validation.validate("bob101*@gmail.com").isLeft) &&
      assertTrue(validation.validate("_").isLeft) &&
      assertTrue(validation.validate("@").isLeft) &&
      assertTrue(validation.validate("@.").isLeft) &&
      assertTrue(validation.validate("b@.com").isLeft) &&
      assertTrue(validation.validate("").isLeft) &&
      assertTrue(validation.validate("1@.com").isLeft) &&
      assertTrue(validation.validate("1@a.com").isLeft) &&
      assertTrue(validation.validate("1@a.b.com").isLeft) &&
      assertTrue(validation.validate("a@a..b.com").isLeft)
    },
    test("Regex phone number Validation for CH") {
      val validation = Validation.phoneNumberCh

      assertTrue(validation.validate("0041791234567").isRight) &&
      assertTrue(validation.validate("0041 79 123 45 67").isRight) &&
      assertTrue(validation.validate("+41791234567").isRight) &&
      assertTrue(validation.validate("+41 79 123 45 67").isRight) &&
      assertTrue(validation.validate("0791234567").isRight) &&
      assertTrue(validation.validate("-41 79 123 45 67").isLeft) &&
      assertTrue(validation.validate("+41 79 123 45 678").isLeft) &&
      assertTrue(validation.validate("79 123 45 678").isLeft)
    },
    test("Regex phone number Validation for DE") {
      val validation = Validation.phoneNumberDe

      assertTrue(validation.validate("+49 30 901820").isRight) &&
      assertTrue(validation.validate("004930901820").isRight) &&
      assertTrue(validation.validate("030901820").isRight) &&
      assertTrue(validation.validate("030 901820").isRight) &&
      assertTrue(validation.validate("+49 1522 3433333").isRight) &&
      assertTrue(validation.validate("+49 152 901820").isRight) &&
      assertTrue(validation.validate("0049 152 901820").isRight) &&
      assertTrue(validation.validate("+49 152 901820").isRight) &&
      assertTrue(validation.validate("0041 30 901820").isLeft) &&
      assertTrue(validation.validate("+49 0152 901820").isLeft) &&
      assertTrue(validation.validate("49 152 901820").isLeft) &&
      assertTrue(validation.validate("049 152 901820").isLeft)
    },
    test("Regex phone number Validation for HU") {
      val validation = Validation.phoneNumberHu

      assertTrue(validation.validate("003612318855").isRight) &&
      assertTrue(validation.validate("0036 1 231 88 55").isRight) &&
      assertTrue(validation.validate("0036 1 231 8855").isRight) &&
      assertTrue(validation.validate("+3611234567").isRight) &&
      assertTrue(validation.validate("+36 1 123 45 67").isRight) &&
      assertTrue(validation.validate("+36 1 123 4567").isRight) &&
      assertTrue(validation.validate("0611234567").isRight) &&
      assertTrue(validation.validate("0036-30-231-88-55").isRight) &&
      assertTrue(validation.validate("0036-30-231-8855").isRight) &&
      assertTrue(validation.validate("+36301234567").isRight) &&
      assertTrue(validation.validate("+36-30-123-45-67").isRight) &&
      assertTrue(validation.validate("+36-30-123-4567").isRight) &&
      assertTrue(validation.validate("06301234567").isRight) &&
      assertTrue(validation.validate("+36 11 123 45 67").isLeft) &&
      assertTrue(validation.validate("+36 5 123 45 67").isLeft) &&
      assertTrue(validation.validate("-36 1 123 45 67").isLeft) &&
      assertTrue(validation.validate("+36 1 123 45 678").isLeft) &&
      assertTrue(validation.validate("1 123 45 678").isLeft) &&
      assertTrue(validation.validate("-36-30-123-45-67").isLeft) &&
      assertTrue(validation.validate("+36-30-123-45-678").isLeft) &&
      assertTrue(validation.validate("30-123-45-678").isLeft)
    },
    test("Time Validation HH") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, "", NoMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "HH")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation H") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, "", NoMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "H")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation mm") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(NoHour, "", HasMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "mm")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation m") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(NoHour, "", HasMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "m")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HHmm") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, "", HasMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "HHmm")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, "", NoSecond, "", NoFraction, "", NoAmPm), "HH:mm")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm:ss") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, "", NoAmPm), "HH:mm:ss")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm:ss a") {
      val parsedTimes = parseTimes(
        CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, " ", HasAmPm),
        "HH:mm:ss a"
      )
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation H:m:s") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, "", NoAmPm), "H:m:s")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation H:m:s a") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, " ", HasAmPm), "H:m:s a")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation hh:mm:ss") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, "", NoAmPm), "hh:mm:ss")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation hh:mm:ss a") {
      val parsedTimes = parseTimes(
        CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, " ", HasAmPm),
        "hh:mm:ss a"
      )
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation h:m:s") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, "", NoAmPm), "h:m:s")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation h:m:s a") {
      val parsedTimes =
        parseTimes(CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, "", NoFraction, " ", HasAmPm), "h:m:s a")
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm:ss S") {
      val parsedTimes = parseTimes(
        CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, " ", HasFraction, "", NoAmPm),
        "HH:mm:ss S"
      )
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm:ss.SSS") {
      val parsedTimes = parseTimes(
        CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, ".", HasFraction, "", NoAmPm),
        "HH:mm:ss.SSS"
      )
      assertParsedTimes(parsedTimes)
    },
    test("Time Validation HH:mm:ss SSSSSSSSS a") {
      val parsedTimes = parseTimes(
        CreateTimesConfig(HasHour, ":", HasMinute, ":", HasSecond, " ", HasFraction, " ", HasAmPm),
        "HH:mm:ss SSSSSSSSS a"
      )
      assertParsedTimes(parsedTimes)
    }
  )

  private def assertParsedTimes(parsedTimes: ParsedTimes) =
    parsedTimes.enoughParsed && parsedTimes.allParseResultsCorrect && parsedTimes.allWrongNotParsed && parsedTimes.allWrongParseResultsCorrect

  private case class ParsedTimes(properTimeResults: Seq[ParsedTime], wrongTimeResults: Seq[ParsedTime]) {

    def enoughParsed: Assert =
      assertTrue(properTimeResults.count(result => result.parsed) >= properTimeResults.size / 4)
    def allParseResultsCorrect: Assert = assertTrue(properTimeResults.forall(result => result.valid == result.parsed))
    def allWrongNotParsed: Assert      = assertTrue(wrongTimeResults.forall(result => !result.valid))

    def allWrongParseResultsCorrect: Assert =
      assertTrue(wrongTimeResults.forall(result => result.valid == result.parsed))
  }

  private case class ParsedTime(time: String, valid: Boolean, parsed: Boolean)

  private def parseTimes(createTimesConfig: CreateTimesConfig, format: String): ParsedTimes = {
    val times      = createTimes(exampleTimes, createTimesConfig)
    val wrongTimes = createTimes(wrongExampleTimes, createTimesConfig)
    val validation = Validation.time(format)
    val formatter  = DateTimeFormatter.ofPattern(format)

    def innerParse(times: Seq[String]) =
      times.map { time =>
        val valid  = validation.validate(time).isRight
        val parsed = Try(formatter.parse(time)).isSuccess
        ParsedTime(time, valid, parsed)
      }

    val results          = innerParse(times)
    val wrongTimeResults = innerParse(wrongTimes)

    ParsedTimes(results, wrongTimeResults)
  }

  private case class ExampleTime(hour: String, minute: String, second: String, fraction: String, amPm: String)

  private val exampleTimes =
    Seq(
      ExampleTime("0", "0", "0", "0", "AM"),
      ExampleTime("1", "1", "1", "1", "AM"),
      ExampleTime("00", "00", "00", "0", "AM"),
      ExampleTime("00", "00", "00", "000000000", "AM"),
      ExampleTime("01", "01", "00", "1", "AM"),
      ExampleTime("01", "01", "00", "000000001", "AM"),
      // ExampleTime("01", "01", "00", "", "PM"), // TODO - good syntax but invalid time for format "HH:mm:ss a" and "H:m:s a"
      ExampleTime("10", "00", "00", "123", "AM"),
      ExampleTime("12", "00", "00", "0", "PM"),
      ExampleTime("12", "09", "09", "000", "PM"),
      ExampleTime("12", "59", "59", "555", "PM"),
      ExampleTime("13", "09", "09", "100000000", "PM"),
      ExampleTime("20", "20", "20", "123456789", "PM"),
      ExampleTime("22", "22", "22", "2", "PM"),
      ExampleTime("23", "59", "00", "222222222", "PM"),
      ExampleTime("23", "59", "59", "999", "PM"),
      ExampleTime("0", "00", "00", "00000", "AM"),
      ExampleTime("1", "11", "00", "000", "AM"),
      // ExampleTime("24", "00", "00", "", "PM"), // TODO - good syntax but invalid time for format "HH:mm:ss a" and "H:m:s a"
      ExampleTime("24", "00", "00", "000000000", "AM")
    )

  private val wrongExampleTimes =
    Seq(
      ExampleTime("", "", "", "", ""),
      ExampleTime("-1", "-1", "-1", "-1", "-"),
      ExampleTime("25", "60", "60", "1000000000", "PM"),
      ExampleTime("90", "90", "90", "9999999999", "PM"),
      ExampleTime("05-05", "05-05", "05-05", "05-05", "PM"),
      ExampleTime("0505", "0505", "0505", "050505050505", "PM"),
      ExampleTime("123", "123", "123", "1231231231", "AM"),
      ExampleTime("111", "111", "111", "0000000001", "XX")
    )

  sealed private trait Hour
  private object Hour {
    case object HasHour extends Hour
    case object NoHour  extends Hour
  }

  sealed private trait Minute
  private object Minute {
    case object HasMinute extends Minute
    case object NoMinute  extends Minute
  }

  sealed private trait Second
  private object Second {
    case object HasSecond extends Second
    case object NoSecond  extends Second
  }

  sealed private trait Fraction
  private object Fraction {
    case object HasFraction extends Fraction
    case object NoFraction  extends Fraction
  }

  sealed private trait AmPm
  private object AmPm {
    case object HasAmPm extends AmPm
    case object NoAmPm  extends AmPm
  }

  private case class CreateTimesConfig(
    hasHour: Hour,
    minuteSeparator: String = "",
    hasMinute: Minute,
    secondSeparator: String = "",
    hasSecond: Second,
    fractionSeparator: String = "",
    hasFraction: Fraction,
    amPmSeparator: String = "",
    hasAmPm: AmPm
  )

  private def createTimes(
    exampleTimes: Seq[ExampleTime],
    config: CreateTimesConfig
  ): Seq[String] =
    exampleTimes.map {
      case ExampleTime(hour, minute, second, fraction, amPm) =>
        val hourStr = config.hasHour match {
          case HasHour => hour
          case NoHour  => ""
        }
        val minuteStr = config.hasMinute match {
          case HasMinute => minute
          case NoMinute  => ""
        }
        val secondStr = config.hasSecond match {
          case HasSecond => second
          case NoSecond  => ""
        }
        val fractionStr = config.hasFraction match {
          case HasFraction => fraction
          case NoFraction  => ""
        }
        val amPmStr = config.hasAmPm match {
          case HasAmPm => amPm
          case NoAmPm  => ""
        }
        s"$hourStr${config.minuteSeparator}$minuteStr${config.secondSeparator}$secondStr${config.fractionSeparator}$fractionStr${config.amPmSeparator}$amPmStr"
    }

}