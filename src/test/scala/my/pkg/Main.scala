package my.pkg

import org.slf4j._

import scala.trace.{Debug, _}


// wildcard import for implicit trace/assert/print functionality



/**
  * Created by johnreed on 3/23/16. Run with sbt test:run
  */
object Main {

def func() = {
  val logger = LoggerFactory.getLogger("Logger")
  logger.warn("A warning has occured" + Pos())
  logger.debug("Value of 2 is " + 2 + Pos()) // append position
}

  def main(args: Array[String]) {

    func()

    import scala.trace.Log

    Debug.trace("Does it work?", 3);

    System.err.println("\n" + Log.pos("foo"))
    val arrayy = Array("hello","world")
    System.err.println("\n" + Log.find(arrayy))
    System.err.println("\n" + Log.find("foooooo"))
    val array = Array("z","z","z","d","a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d")
    System.err.println("\n" + Log.find(array))

    Debug.trace("Trace to standard error")
    Thread.sleep(10);

    Debug.traceStdOut("Trace to standard out")
    Thread.sleep(10)
    Debug.traceStdOut("0 lines of trace", 0)
    Debug.traceStdOut("2 lines of trace", 2)
    Thread.sleep(10)
    Debug.check(7 == 8, "assertion failures are bright red", 1)
    val array1: Array[String] = Array("1", "2", "3")
    Debug.traceArray(array1, 0, 3, 1)
    Debug.assrt(7 == 8, "assert is fatal, check is not", 1)


    SDebug.traceContents(List(1,2,3,4))
    SDebug.traceContents(Map("1"->1, "2"->2))
    Thread.sleep(10)
    SDebug.traceContents(List(1,2,3,4,5,1,2,3,4,5,1,2,3,4,5))

    Debug.traceArray(Array("a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d"))
    Debug.traceArray(Array("a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d"))
    Debug.trace("foo")

    Debug.traceArray(Array("a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d","a","b","c","d"))
    Debug.trace("foo")

    SDebug.assertCode( // woo
      { // woo0000t
        "foo" == 2}, 3
    )

    Debug.trace("foo1")
    Debug.trace("foo3", 3)
    Debug.traceStdOut("foo1StdOut")
    Debug.traceStdOut("foo3StdOut", 3)

    Debug.enableEverything()
    Debug.disableEverything

    Debug.fatalAssertOff()
    Debug.fatalAssertOn()

    Debug.nonFatalAssertOff()
    Debug.nonFatalAssertOn()

    Debug.traceErrOff()
    Debug.traceErrOn()

    Debug.traceOutOff()
    Debug.traceOutOn()

    Debug.trace("foo1")
    Debug.trace("foo3", 3)
    Debug.traceStdOut("foo1StdOut")
    Debug.traceStdOut("foo3StdOut", 3)

    // Easy to locate log statements
    SDebug.traceContents(List(1, 2, 3) map ( 2.* ))
    SDebug.traceContents(List(1, 2, 3) map ( _*2 ))
  }
}


    /*
val logger = LoggerFactory.getLogger("Logger");
logger.warn(Log.pos("foobar")) // append position
logger.warn(Log.find("foo" + 2 + "bar"))
logger.warn(Log.find(Array("foo", "bar")))
val list = List(0, 1, 2)
logger.warn(Log.find(list, 2)) // first 2 elements
logger.warn(Log.find(Map("foo" -> 2)))


    println()
    println()
    println()
    logger.warn(Log.find(Array("foo", "bar", "baz")))
    logger.warn(Log.find(Array("foo", "bar", "baz"), 1))
    logger.warn(Log.find(Array("foo", "bar", "baz"), 0, 2))






    SDebug.traceContents(List(0, 1, 2, 3), numElements = 3)

    //val file = sourcecode.File()
    //assert(file.endsWith("/sourcecode/shared/src/test/scala/sourcecode/Tests.scala"))

    /*
    Debug.traceCode(file)

    val line = implicitly[sourcecode.Line]

    Debug.traceCode(line)

    val s = sourcecode.FullName.Machine()

    Debug.traceCode( implicitly[sourcecode.Name] )
    Debug.traceCode( implicitly[sourcecode.FullName.Machine] )

    Thread.sleep(10)
    println()
    val parseFile: (String => String) = (s: String) => {
      val parsedFileLinuxMac = s.split("/")
      // val parsedFileWindows = s.split("\\") // java.util.regex.PatternSyntaxException: Unexpected internal error
      parsedFileLinuxMac(parsedFileLinuxMac.length-1)
    }
    println( sourcecode.FullName.Machine() + "(" + parseFile( sourcecode.File() ) + ":" + sourcecode.Line() + ")" )
    println( sourcecode.Name.Machine() + "(" + sourcecode.File() + ":" + sourcecode.Line() + ")" )
    println( sourcecode.Enclosing.Machine() + "(" + sourcecode.File() + ":" + sourcecode.Line() + ")" )
    println()
    */
    Thread.sleep(10)

    System.err.println("Main.main(Main.scala:30)")

    logger.debug("Hello world.");
    logger.debug("Hello world.");
    logger.debug("Hello world.");
    Debug.disableEverythingSE()
    logger.debug(Debug.trace("Foo bar is awesome!"))
    logger.debug(Debug.trace("Foo bar is awesome!"))
    logger.debug(Debug.trace("Foo bar is awesome!"))
    logger.debug("foo", new RuntimeException("re"))
    Debug.enableEverythingSE()
    SDebug.traceContents(List(1, 2, 3, 4, 5, 6, 7))

    val temp = LoggerFactory.getILoggerFactory();
    val lc = temp.asInstanceOf[LoggerContext]
    StatusPrinter.print(lc);

    Thread.sleep(10)

    /*
    Debug.traceCode({
      // print out the code as it appears in the source
      val myVal = 4;
      1 + 2 + myVal
    }, 1) // 1 lines of stack trace

    Debug.traceCode({
      // print out the code as it appears in the source
      val s = List(1, 2, 3)
      (s).toString
    }, 2) // 2 lines of stack trace

    Debug.checkCode({
      val three = 3
      three + 2 == 0
    }, 2)

    Debug.assertCode {
      val one = 1
      one + 2 == 3
    }
    val m = Map[String, Int]("hello" -> 2) //  TraversableLike[A, Traversable[A]]

    Thread.sleep(20)

    // You can use this with a logger
    Debug.traceErrOffSE // just get the String
    val collectionString = SDebug.traceContents(List(1, 2, 3))
    println(collectionString)

    Debug.traceOutOnSE
    SDebug.traceContentsStdOut(Map("1" -> 1, "2" -> 2, "3" -> 3))

    Debug.enableEverythingSE
    SDebug.traceContents(List(1, 2, 3), numElements = 2, numLines = 2)


    Thread.sleep(20)
    */

    /*
    val ll = new java.util.List() // interface Iterable<T>
    val lll = new java.util.Set() // Iterable<T>
    val llll = new java.util.Map() // Map<K, V>
    val x = new ConcurrentHashMap[String, Int]() // AbstractMap<K, V>
    val list = new scala.collection.mutable.ArrayBuffer[Int]()
val m = Map[String, Int]("hello" -> 2) //  TraversableLike[A, Traversable[A]]
val r = Array.apply(1,2,3) //  Array[T]
    val l = List(1,2,3) // A template trait for traversable collections of type Traversable[A]. This is a base trait of all kinds of Scala collections.
    */
    Debug.trace("Hello World") // 1 line of stack trace
    Debug.trace("Hello World 2", numLines = 2) // 2 lines of stack trace
    "Hello World".trace(numLines = 0) // 0 lines of stack trace

    Debug.traceExpression {
      // trace the expression ("foo" + "bar")
      val foo = "foo";
      foo + "bar"
    }
    Debug.assert(1 + 1 == 2, "One plus one must equal two") // fatal assertion (kills application)
    1.checkEquals(3, "One must equal three", numLines = 2) // non-fatal assertion, 2 lines of stack trace

    Thread.sleep(10) // sleep to prevent print statements from getting mixed up

    Debug.traceStdOut("Hey0")
    Debug.traceStdOutExpression {
      "Hey3"
    }
    Debug.traceStdOutExpression({
      val myVal = 5;
      1 + 2 + myVal
    }, 0) // 0 lines of stack trace
    Debug.traceStdOutExpression("Hey4", 2)
    Debug.traceStackStdOutExpression {
      val myVal = 6;
      1 + 2 + myVal
    }

    Thread.sleep(10) // sleep to prevent print statements from getting mixed up

    Debug.traceExpression {
      "Hey5"
    }
    Debug.traceExpression {
      val myVal = 3;
      1 + 2 + myVal
    }
    Debug.traceExpression("Hey6", 2)
    Debug.traceStackExpression {
      val myVal = 4;
      1 + 2 + myVal
    }
    Debug.traceExpression {
      val one = 1;
      val two = 2;
      val three = 3;
      val four = 4;
      val five = 5;
      one * two + three / four + five;
    }
    System.err.println("Assertions return strings: " + Debug.checkExpression({
      val someVal = 2;
      1 + someVal == 4
    }, 3))
    Debug.checkExpression {
      val someVal = 2;
      1 + someVal == 4
    }
    Debug.assertExpression({
      val one = 1;
      one + 1 == 2
    }, 0) // 0 lines of stack trace
    Debug.assertExpression {
      val myVal = 3;
      1 + 2 == myVal
    }
    Debug.assertExpression(1 + 2 == 3)
    Debug.checkExpression({
      val noStack = "No stack trace is generated";
      noStack.equals("foo")
    }, 0) // no lines of stack trace
    Debug.check(1 == 2, "No stack trace is printed", 0)

    Debug.enableEverythingSE()
    System.err.println(Debug.trace(77, numLines = 2)) // this should return a String
    System.err.println(Debug.check(2 == 3, "foo", numLines = 20)) // this should return a String
    val fooVar = "foo"
    val barVar = "bar"
    Debug.traceCode[String] {
      fooVar + barVar
    }
    Debug.traceCode[String]({
      fooVar + barVar
    }, 3)
    Debug.traceStackCode[String] {
      fooVar + barVar
    }
    val trueVar = true
    val falseVar = false
    System.err.println("assertCode")
    // Debug.assertCode{(trueVar || falseVar || false) && false} // "trueVar || falseVar || false -> false" in thread run-main-4:
    System.err.println("End")
  }
}
*/