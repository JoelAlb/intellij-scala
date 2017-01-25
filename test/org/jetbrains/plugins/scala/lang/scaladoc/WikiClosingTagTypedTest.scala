package org.jetbrains.plugins.scala
package lang.scaladoc

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.plugins.scala.base.{EditorActionTestAdapter, ScalaLightCodeInsightFixtureTestAdapter}

/**
 * User: Dmitry Naydanov
 * Date: 2/25/12
 */
class WikiClosingTagTypedTest extends ScalaLightCodeInsightFixtureTestAdapter with EditorActionTestAdapter {

  import CodeInsightTestFixture.CARET_MARKER

  def testCodeLinkClosingTagInput() {
    val text = "/** [[java.lang.String" + CARET_MARKER + "]] */"
    val assumedStub = "/** [[java.lang.String]" + CARET_MARKER + "] */"

    checkGeneratedTextAfterTyping(text, assumedStub, ']')
  }

  def testInnerCodeClosingTagInput() {
    val text =
      ("""
    |  /**
    |    *
    |    * {{{
    |    *  class A {
    |    *    def f() {}
    |    * } }}""" + CARET_MARKER + """}
    |    */
                                    """).stripMargin

    val assumedStub =
      ("""
    |  /**
    |    *
    |    * {{{
    |    *  class A {
    |    *    def f() {}
    |    * } }}}""" + CARET_MARKER + """
    |    */
                                     """).stripMargin

    checkGeneratedTextAfterTyping(text, assumedStub, '}')
  }

  def testItalicClosingTagInput() {
    val text =
      ("""
      | /**
      |   * ''blah blah blah blah
      |   *   blah blah blah '""" + CARET_MARKER + """'
      |   */
                                                   """).stripMargin

    val assumedStub =
      ("""
      | /**
      |   * ''blah blah blah blah
      |   *   blah blah blah ''""" + CARET_MARKER + """
      |   */
                                                    """).stripMargin

    checkGeneratedTextAfterTyping(text, assumedStub, '\'')
  }

  def testSuperscriptClosingTagInput() {
    val text = "/** 2^2" + CARET_MARKER + "^ = 4 */"
    val assumedStub = "/** 2^2^" + CARET_MARKER + " = 4 */"

    checkGeneratedTextAfterTyping(text, assumedStub, '^')
  }

  def testMonospaceClosingTag() {
    val text =
      ("""
      | /**
      |   * `blah-blah""" + CARET_MARKER + """`
      |   */
                                           """).stripMargin

    val assumedStub =
      ("""
      | /**
      |   * `blah-blah`""" + CARET_MARKER + """
      |   */
                                            """).stripMargin

    checkGeneratedTextAfterTyping(text, assumedStub, '`')
  }

  def testBoldClosingTag() {
    val text = "/** '''blah blah blah'" + CARET_MARKER + "'' */"
    val assumedStub = "/** '''blah blah blah''" + CARET_MARKER + "' */"

    checkGeneratedTextAfterTyping(text, assumedStub, '\'')
  }

  def testUnderlinedClosingTag() {
    val text =
      ("""
      | /**
      |   * __blah blahblahblahblahblah
      |   *       blah blah blah blah""" + CARET_MARKER + """__
      |   */
                                                          """).stripMargin

    val assumedStub =
      ("""
      | /**
      |   * __blah blahblahblahblahblah
      |   *       blah blah blah blah_""" + CARET_MARKER + """_
      |   */
                                                           """).stripMargin

    checkGeneratedTextAfterTyping(text, assumedStub, '_')
  }

  def testBoldTagEmpty() {
    val text = "/** '''" + CARET_MARKER + "''' */"
    val assumedStub = "/** ''''" + CARET_MARKER + "'' */"

    checkGeneratedTextAfterTyping(text, assumedStub, '\'')
  }
}
