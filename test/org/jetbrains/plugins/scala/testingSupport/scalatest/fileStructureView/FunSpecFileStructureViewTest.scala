package org.jetbrains.plugins.scala.testingSupport.scalatest.fileStructureView

import org.jetbrains.plugins.scala.lang.structureView.elements.impl.TestStructureViewElement._
import org.jetbrains.plugins.scala.testingSupport.scalatest.ScalaTestTestCase

/**
  * @author Roman.Shein
  * @since 20.04.2015.
  */
trait FunSpecFileStructureViewTest extends ScalaTestTestCase {
  private val className = "FunSpecViewTest"

  addSourceFile(className + ".scala",
    s"""
      |import org.scalatest._
      |
      |class $className extends FunSpec {
      |  describe("parent") {
      |    it ("child1") {}
      |
      |    ignore ("ignore1") {}
      |
      |    they ("child2") (pending)
      |  }
      |
      |  describe("pending") (pending)
      |  ignore("pending_and_ignore") (pending)
      |}
    """.stripMargin)

  def testFunSpecNormal() {
    runFileStructureViewTest(className, normalStatusId, "describe(\"parent\")", "it (\"child1\")")
  }

  def testFunSpecHierarchy(): Unit = {
    runFileStructureViewTest(className, "it (\"child1\")", Some("describe(\"parent\")"))
  }

  def testFunSpecIgnored(): Unit = {
    runFileStructureViewTest(className, ignoredStatusId, "ignore (\"ignore1\")")
  }

  def testFunSpecIgnoredAndPending(): Unit = {
    runFileStructureViewTest(className, ignoredStatusId, "ignore(\"pending_and_ignore\")")
  }

  def testFunSpecPending(): Unit = {
    runFileStructureViewTest(className, pendingStatusId, "describe(\"pending\")", "they (\"child2\")")
  }
}
