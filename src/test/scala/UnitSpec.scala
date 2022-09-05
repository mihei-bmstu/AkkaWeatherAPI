import org.scalatest._
import matchers._
import org.scalatest.funsuite.AnyFunSuite

abstract class UnitSpec extends AnyFunSuite
  with should.Matchers
  with OptionValues
