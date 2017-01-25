package org.jetbrains.plugins.scala
package lang.scaladoc

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.plugins.scala.base.{EditorActionTestAdapter, ScalaLightCodeInsightFixtureTestAdapter}

/**
 * User: Dmitry Naydanov
 * Date: 2/27/12
 */
class WikiPairedTagBackspaceTest extends ScalaLightCodeInsightFixtureTestAdapter with EditorActionTestAdapter {

  import CodeInsightTestFixture.CARET_MARKER
  
  def testDeleteUnderlinedTag() {
    checkGeneratedTextAfterBackspace("/** __" + CARET_MARKER + "blah blah__ */", "/** _blah blah */")
  }

  def testDeleteMonospaceTag() {
    checkGeneratedTextAfterBackspace("/** `" + CARET_MARKER + "blahblah` */", "/** blahblah */")
  }

  def testDeleteItalicTag() {
    checkGeneratedTextAfterBackspace("/** ''" + CARET_MARKER + "blah blah'' */", "/** 'blah blah */")
  }

  def testDeleteBoldTag() {
    checkGeneratedTextAfterBackspace("/** '''" + CARET_MARKER + "blah blah''' */", "/** ''blah blah'' */")
  }

  def testDeleteSubscriptTag() {
    checkGeneratedTextAfterBackspace("/** ,," + CARET_MARKER + "blah blah,, */", "/** ,blah blah */")
  }

  def testScl6717() {
    checkGeneratedTextAfterBackspace(
      s"""
         | /**
         |  * a =$CARET_MARKER b
         |  */
       """.stripMargin,
      s"""
         | /**
         |  * a $CARET_MARKER b
         |  */
       """.stripMargin
    )
  }

  def testDeleteInnerCodeTag() {
    val text =
      ("""
      | /**
      |   * {{{""" + CARET_MARKER + """
      |   *   class A {
      |   *     def f () {}
      |   * }
      |   *}}}
      |   */
                                    """).stripMargin
    val assumedStub =
      """
      | /**
      |   * {{
      |   *   class A {
      |   *     def f () {}
      |   * }
      |   *
      |   */
      """.stripMargin

    checkGeneratedTextAfterBackspace(text, assumedStub)
  }

  def testDeleteCodeLinkTag() {
    checkGeneratedTextAfterBackspace("/** [[" + CARET_MARKER + "java.lang.String]] */", "/** [java.lang.String */")
  }

  def testDeleteEmptyItalicTag() {
    checkGeneratedTextAfterBackspace("/** ''" + CARET_MARKER + "'' */", "/** ' */")
  }
}
