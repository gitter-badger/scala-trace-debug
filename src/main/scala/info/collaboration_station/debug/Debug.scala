package info.collaboration_station.debug

import info.collaboration_station.debug.internal.Printer

import scala.language.experimental.macros

/**
  * Created by johnreed on 3/12/16. Contains static debug functions.
  */
object Debug {

  /**
    * Stack offset is 2 because the first row in the stack trace is Thread and the second row is internal call
    */
  protected[debug] val stackOffset = 2 + 1 // this +1 is temporary for the extra method invocation.

  @volatile private var _traceOutOn_? = true
  @volatile private var _traceErrOn_? = true
  @volatile private var _fatalAssertOn_? = true
  @volatile private var _nonFatalAssertOn_? = true

  /** Tells you whether tracing to standard out is on or off
    * Note that disabling the "traceStdOut" feature does not disable the "assertStdOut" feature
    */
  def traceOutOn_? = _traceOutOn_?

  /** Tells you whether tracing to standard error is on or off
    * Note that disabling the "trace" feature does not disable the "assert" feature
    */
  def traceErrOn_? = _traceErrOn_?

  /**
    * Tells you whether fatal asserts are on or off
    */
  def fatalAssertOn_? = _fatalAssertOn_?

  /**
    * Tells you whether non-fatal asserts are on or off
    */
  def nonFatalAssertOn_? = _nonFatalAssertOn_?

  // these lines disable and enable particular features

  /**
    * Enables tracing and asserts, including fatal assertions
    */
  def enableEverything_!() = {
    traceErrOn_!()
    traceOutOn_!()
    fatalAssertOn_!()
    nonFatalAssertOn_!()
  }

  /**
    * Enables tracing to standard error. Has no effect on "print" or "println", only on "trace" methods
    */
  def traceErrOn_!() = {
    _traceErrOn_? = true
  }

  /**
    * Enables tracing to standard out. Has no effect on "print" or "println", only on "traceStdOut" methods
    */
  def traceOutOn_!() = {
    _traceOutOn_? = true
  }

  /**
    * Enables fatal assertions. Has no effect on "safeAssert", only on regular "assert" and other regular assert methods (assertEquals, etc.)
    */
  def fatalAssertOn_!() = {
    _fatalAssertOn_? = true
  }

  /**
    * Enables non-fatal assertions. Has no effect on "assert" and other fatal assert methods (assertEquals, etc.)
    */
  def nonFatalAssertOn_!() = {
    _nonFatalAssertOn_? = true
  }

  /**
    * Disables tracing and asserts. Both fatal and non-fatal assertions are disabled. Does not disable print or println
    */
  def disableEverything_!() = {
    traceErrOff_!()
    traceOutOff_!()
    fatalAssertOff_!()
    nonFatalAssertOff_!()
  }

  /**
    * Disables tracing to standard error. Has no effect on "print" or "println", only on "trace" methods
    */
  def traceErrOff_!() = {
    _traceErrOn_? = false
  }

  /**
    * Disables tracing to standard out. Has no effect on "print" or "println", only on "traceStdOut" methods
    */
  def traceOutOff_!() = {
    _traceOutOn_? = false
  }

  /**
    * Disables fatal assertions. Has no effect on "safeAssert", only on "assert" and other fatal assert methods (assertEquals, etc.)
    */
  def fatalAssertOff_!() = {
    _fatalAssertOn_? = false
  }

  /**
    * Disables non-fatal assertions. Has no effect on "assert" and other fatal assert methods (assertEquals, etc.)
    */
  def nonFatalAssertOff_!() = {
    _nonFatalAssertOn_? = false
  }

  /**
    * Traces to standard error with a one line stack trace.
    *
    * @param block this block contains or returns whatever it is to be traced.
    * @tparam T the return type of the block
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def trace[T](block: => T): String = Printer.traceInternal(block.toString, 1)

  /**
    * Traces to standard error with a N line stack trace.
    *
    * @param block    this block contains or returns whatever it is to be traced.
    * @param numLines the number of lines to trace
    * @tparam T the return type of the block
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def trace[T](block: => T, numLines: Int): String = Printer.traceInternal(block.toString, numLines)

  /**
    * Traces to standard error with a full length stack trace.
    *
    * @param block this block contains or returns whatever it is to be traced.
    * @tparam T the return type of the block
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def traceStack[T](block: => T): String = Printer.traceInternal(block.toString, Int.MaxValue)

  /**
    * Same as Debug.trace, but prints to standard out instead of standard error
    *
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def traceStdOut[T](block: => T): String = Printer.traceInternal(block.toString, 1, useStdOut_? = true)

  /**
    * Same as Debug.trace(block: => T, numLines: Int), but prints to standard out instead of standard error
    *
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def traceStdOut[T](block: => T, numLines: Int): String = Printer.traceInternal(block.toString, numLines, useStdOut_? = true)

  /**
    * Same as traceStack, but prints to StdOut instead of StdError
    *
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def traceStackStdOut[T](block: => T): String = Printer.traceInternal(block.toString, Int.MaxValue, useStdOut_? = true)

  /** A fatal assertion.
    * Terminates the program with exit code "7"
    *
    * @param assertion the assertion that must be true for the program to run. Can be a value or a function
    * @param message   the message to be printed to standard error on assertion failure
    * @example Debug.assert( 1 + 2 == 4, "Error: one plus two is not equal to four" )
    * @note this (and other assertions not marked "safe") are fatal. To disable, please call "Debug.fatalAssertOff_!()"
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def assert(assertion: => Boolean, message: String, numLines: Int = Int.MaxValue): String = {
    Printer.internalAssert(message, numLines, useStdOut_? = false, assertionTrue_? = assertion, isFatal_? = true) // trace the max number of lines of stack trace to std error
  }

  /** A fatal assertion.
    * Terminates the program with exit code "7"
    *
    * @param assertion the assertion that must be true for the program to run. Can be a value or a function
    * @param message   the message to be printed to standard out on assertion failure
    * @example Debug.assertStdOut( 1 + 2 == 4, "Error: one plus two is not equal to four" )
    * @note this (and other assertions not marked "safe") are fatal. To disable, please call "Debug.fatalAssertOff_!()"
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def assertStdOut(assertion: => Boolean, message: String, numLines: Int = Int.MaxValue): String = {
    Printer.internalAssert(message, numLines, useStdOut_? = true, assertionTrue_? = assertion, isFatal_? = true)
  }

  /**
    * Like Debug.assert(), but does not terminate the application
    *
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def safeAssert(assertion: => Boolean, message: String, numLines: Int = Int.MaxValue): String = {
    Printer.internalAssert(message, numLines, useStdOut_? = false, assertionTrue_? = assertion, isFatal_? = false)
  }

  /**
    * Like Debug.assertStdOut(), but does not terminate the application
    *
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final def safeAssertStdOut(assertion: => Boolean, message: String, numLines: Int = Int.MaxValue): String = {
    Printer.internalAssert(message, numLines, useStdOut_? = true, assertionTrue_? = assertion, isFatal_? = false)
  }

  import scala.collection.TraversableLike
  import scala.reflect.runtime.universe.WeakTypeTag

  /**
    * Traces the contents of a Scala container to standard error. To convert a Java container into a Scala container, import collection.JavaConversions._
    *
    * @param collection  the Scala collection. TraversableLike is a base trait of all kinds of Scala collections.
    * @param numElements the number of elements you want to trace. Defaults to all elements in the collection
    * @param numLines    the number of lines of stack trace.
    * @return the string containing what was printed or what would have been printed if printing was enabled.
    * @example Debug.contents( List(1,2,3) )
    */
  final def contents[CollectionType <: TraversableLike[Any, Any]]
  (collection: CollectionType, numElements: Int = Int.MaxValue, numLines: Int = 1)(implicit tag: WeakTypeTag[CollectionType]): String = {
    val collectionType = tag.tpe
    var toPrint = collectionType.toString
    val numValues = Math.min(numElements, collection.size)
    val iterator = collection.toIterator
    for (index <- 0 to numValues - 1) {
      // -1 because we are starting from 0 instead of 1
      if (iterator.hasNext) {
        toPrint = toPrint + " " + iterator.next()
      }
    }
    Printer.traceInternal(toPrint, numStackLinesIntended = numLines, useStdOut_? = false)
  }

  /**
    * Traces the contents of a Scala container to standard out. To convert a Java container into a Scala container, import collection.JavaConversions._
    *
    * @param collection  the Scala collection. TraversableLike is a base trait of all kinds of Scala collections.
    * @param numElements the number of elements you want to trace. Defaults to all elements in the collection
    * @param numLines    the number of lines of stack trace.
    * @return the string containing what was printed or what would have been printed if printing was enabled.
    * @example Debug.contentsStdOut( List(1,2,3) )
    */
  final def contentsStdOut[CollectionType <: TraversableLike[Any, Any]]
  (collection: CollectionType, numElements: Int = Int.MaxValue, numLines: Int = 1)(implicit tag: WeakTypeTag[CollectionType]): String = {
    val collectionType = tag.tpe
    var toPrint = collectionType.toString
    val numValues = Math.min(numElements, collection.size)
    val iterator = collection.toIterator
    for (index <- 0 to numValues - 1) {
      // -1 because we are starting from 0 instead of 1
      if (iterator.hasNext) {
        toPrint = toPrint + " " + iterator.next()
      }
    }
    Printer.traceInternal(toPrint, numStackLinesIntended = numLines, useStdOut_? = true)
  }

  /**
    * Same as trace, but prints the code in the block, not just the result
    *
    * @example myVal = 3; Debug.code{1 + 2 + myVal}
    * @example myVal = 3; Debug.code(1 + 2 + myVal}, 3) // 3 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object code {
    final def codeImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val blockTree = block.tree
      val blockSource = new String(blockTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = blockTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = blockSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = blockSource.slice(start, start + end)
      val arg1 = q""" "(" + $blockString + ") -> " + ({$block}.toString) """
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.trace(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def traceLinesCodeImpl[T](c: Compat.Context)(block: c.Expr[T], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val blockTree = block.tree
      val blockSource = new String(blockTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = blockTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = blockSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = blockSource.slice(start, start + end)
      val arg1 = q""" "(" + $blockString + ") -> " + ({$block}.toString) """
      val arg2 = q"$numLines"
      val args = List(arg1, arg2)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.trace(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro codeImpl[T]

    final def apply[T](block: => T, numLines: Int): String = macro traceLinesCodeImpl[T]
  }

  // You can't pass in : =>Boolean without getting "java.lang.IllegalArgumentException: Could not find proxy for val myVal"
  // You also cannot use default parameters. Boo.

  /**
    * Same as traceStack, but prints the source code in the block, not just the result
    * Debug.codeStack{ // Her this is come code }
    * @example myVal = 3; Debug.codeStack{1 + 2 + myVal}
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object codeStack {
    final def codeStackImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val blockTree = block.tree
      val blockSource = new String(blockTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = blockTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = blockSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = blockSource.slice(start, start + end)
      val arg1 = q""" "(" + $blockString + ") -> " + ({$block}.toString) """
      // System.err.println(arg1)
      // At compile time prints: "(".$plus("fooVar + barVar").$plus(") -> ").$plus(fooVar.+(barVar).toString)
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.traceStack(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro codeStackImpl[T]
  }

  /**
    * Same as trace, but prints the entire expression, not just the result
    *
    * @example Debug.expression{val myVal = 3; 1 + 2 + myVal}
    * @example Debug.expression({val myVal = 3; 1 + 2 + myVal}, 3) // 3 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object expression {
    final def expressionImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.trace(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def traceLinesExpressionImpl[T](c: Compat.Context)(block: c.Expr[T], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val arg2 = q"$numLines"
      val args = List(arg1, arg2)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.trace(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro expressionImpl[T]

    final def apply[T](block: => T, numLines: Int): String = macro traceLinesExpressionImpl[T]
  }

  /**
    * Same as traceStack, but prints the entire expression, not just the result
    *
    * @example Debug.expressionStack{val myVal = 3; 1 + 2 + myVal}
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object expressionStack {
    final def expressionStackImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.traceStack(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro expressionStackImpl[T]
  }

  /**
    * Same as Debug.traceStdOut, but prints the whole expression not just its result
    *
    * @example Debug.expressionStdOut{val myVal = 3; 1 + 2 + myVal}
    * @example Debug.expressionStdOut({val myVal = 3; 1 + 2 + myVal}, 3) // 3 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object expressionStdOut {

    final def expressionStdOutImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.traceStdOut(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def traceLinesStdOutExpressionImpl[T](c: Compat.Context)(block: c.Expr[T], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val arg2 = q"$numLines"
      val args = List(arg1, arg2)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.traceStdOut(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro expressionStdOutImpl[T]

    final def apply[T](block: => T, numLines: Int): String = macro traceLinesStdOutExpressionImpl[T]
  }

  /**
    * Same as traceStackStdOut, but prints the whole expression not just the result
    *
    * @example Debug.expressionStackStdOut{val myVal = 3; 1 + 2 + myVal}
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object expressionStackStdOut {
    final def expressionStackStdOutImpl[T](c: Compat.Context)(block: c.Expr[T]): c.Expr[String] = {
      import c.universe._
      val toTraceString = (block.tree).toString + " -> "
      val arg1 = q"$toTraceString + ({$block}.toString)"
      val args = List(arg1)
      val toReturn =
        q"""
        info.collaboration_station.debug.Debug.traceStackStdOut(..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply[T](block: => T): String = macro expressionStackStdOutImpl[T]
  }

  /**
    * Same as assert, but prints the whole expression instead of an error message
    *
    * @example Debug.assertExpression{val one = 1; one + 1 == 2}
    * @example Debug.assertExpression({val one = 1; one + 1 == 2}, 0) // 0 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object assertExpression {
    final def apply(assertion: Boolean): String = macro assertExpressionImpl

    final def assertExpressionImpl(c: Compat.Context)(assertion: c.Expr[Boolean]): c.Expr[String] = {
      import c.universe._
      val assertionString = (assertion.tree).toString + " -> "
      //val arg1 = q"$assertion"
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"Int.MaxValue"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.assert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply(assertion: Boolean, numLines: Int): String = macro assertLinesExpressionImpl

    final def assertLinesExpressionImpl(c: Compat.Context)(assertion: c.Expr[Boolean], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val assertionString = (assertion.tree).toString + " -> "
      //val arg1 = q"$assertion"
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"$numLines"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.assert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }
  }

  /**
    * Same as assert, but prints the code instead of an error message.
    *
    * @example val one = 1; Debug.assertCode{one + 1 == 2}
    * @example val one = 1; Debug.assertCode({one + 1 == 2}, 0) // 0 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object assertCode {
    final def apply(assertion: Boolean): String = macro assertCodeImpl

    final def assertCodeImpl(c: Compat.Context)(assertion: c.Expr[Boolean]): c.Expr[String] = {
      import c.universe._
      val assertionTree = assertion.tree
      val assertionSource = new String(assertionTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = assertionTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = assertionSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = assertionSource.slice(start, start + end)
      val assertionString = blockString + " -> "
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"Int.MaxValue"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.assert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply(assertion: Boolean, numLines: Int): String = macro assertLinesCodeImpl

    final def assertLinesCodeImpl(c: Compat.Context)(assertion: c.Expr[Boolean], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val assertionTree = assertion.tree
      val assertionSource = new String(assertionTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = assertionTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = assertionSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = assertionSource.slice(start, start + end)
      val assertionString = blockString + " -> "
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"$numLines"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.assert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }
  }

  import scala.collection.TraversableLike
  import scala.reflect.runtime.universe._

  // for WeakTypeTag

  /**
    * Same as safeAssert, but prints the whole expression instead of an error message
    *
    * @example Debug.safeAssertExpression{val one = 1; one + 1 == 2}
    * @example Debug.safeAssertExpression({val one = 1; one + 1 == 2}, 0) // 0 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object safeAssertExpression {

    final def apply(assertion: Boolean): String = macro safeAssertExpressionImpl

    final def safeAssertExpressionImpl(c: Compat.Context)(assertion: c.Expr[Boolean]): c.Expr[String] = {
      import c.universe._
      val assertionString = (assertion.tree).toString + " -> "
      //val arg1 = q"$assertion"
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"Int.MaxValue"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.safeAssert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply(assertion: Boolean, numLines: Int): String = macro safeAssertLinesExpressionImpl

    final def safeAssertLinesExpressionImpl(c: Compat.Context)(assertion: c.Expr[Boolean], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val assertionString = (assertion.tree).toString + " -> "
      //val arg1 = q"$assertion"
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"$numLines"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.safeAssert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }
  }

  /**
    * Same as safeAssert, but prints the code instead of an error message.
    *
    * @example val one = 1; Debug.safeAssertCode{one + 1 == 2}
    * @example val one = 1; Debug.safeAssertCode({one + 1 == 2}, 0) // 0 lines of stack trace
    * @return the string containing what was printed or what would have been printed if printing was enabled. You can pass this string into a logger.
    */
  final object safeAssertCode {
    final def apply(assertion: Boolean): String = macro assertCodeImpl

    final def assertCodeImpl(c: Compat.Context)(assertion: c.Expr[Boolean]): c.Expr[String] = {
      import c.universe._
      val assertionTree = assertion.tree
      val assertionSource = new String(assertionTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = assertionTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = assertionSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = assertionSource.slice(start, start + end)
      val assertionString = blockString + " -> "
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"Int.MaxValue"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.safeAssert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }

    final def apply(assertion: Boolean, numLines: Int): String = macro assertLinesCodeImpl

    final def assertLinesCodeImpl(c: Compat.Context)(assertion: c.Expr[Boolean], numLines: c.Expr[Int]): c.Expr[String] = {
      import c.universe._
      val assertionTree = assertion.tree
      val assertionSource = new String(assertionTree.pos.source.content)
      // apply case tree => tree.pos.start to each subtree on which the function is defined and collect the results.
      val listOfTreePositions: List[Int] = assertionTree.collect { case tree => tree.pos.start }
      val start: Int = listOfTreePositions.min
      import scala.language.existentials
      val globalContext = c.asInstanceOf[reflect.macros.runtime.Context].global // inferred existential
      val codeParser = globalContext.newUnitParser(code = assertionSource.drop(start))
      codeParser.expr()
      val end = codeParser.in.lastOffset
      val blockString = assertionSource.slice(start, start + end)
      val assertionString = blockString + " -> "
      val arg2 = q"$assertionString + ({$assertion}.toString)"
      val arg3 = q"$numLines"
      val args = List(arg2, arg3)
      val toReturn =
        q"""
        val assertBoolean = $assertion;
        info.collaboration_station.debug.Debug.safeAssert(assertBoolean, ..$args);
    """
      c.Expr[String](toReturn)
    }
  }

}