package org.jetbrains.plugins.scala
package base

import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.testFramework.fixtures.CodeInsightTestFixture.CARET_MARKER
import com.intellij.testFramework.fixtures.{CodeInsightTestFixture, LightCodeInsightFixtureTestCase}
import org.jetbrains.plugins.scala.util.TestUtils
import org.jetbrains.plugins.scala.util.TestUtils.ScalaSdkVersion

/**
  * User: Dmitry Naydanov
  * Date: 3/5/12
  */

abstract class ScalaLightCodeInsightFixtureTestAdapter extends LightCodeInsightFixtureTestCase with TestFixtureProvider {

  private var libraryLoaders: Seq[LibraryLoader] = Seq.empty

  override def getFixture: CodeInsightTestFixture = myFixture

  override protected def setUp() {
    super.setUp()

    if (loadScalaLibrary) {
      getFixture.allowTreeAccessForAllFiles()

      implicit val project = getProject
      implicit val module = getFixture.getModule

      libraryLoaders = Seq(ScalaLibraryLoader(isIncludeReflectLibrary = loadReflectLibrary),
        JdkLoader.mock)
    }

    libraryLoaders.foreach(_.init)
  }

  protected override def tearDown(): Unit = {
    libraryLoaders.foreach(_.clean())
    super.tearDown()
  }

  protected implicit def libVersion: ScalaSdkVersion = TestUtils.DEFAULT_SCALA_SDK_VERSION

  protected def loadScalaLibrary: Boolean = true

  protected def loadReflectLibrary: Boolean = false

  protected def checkTextHasNoErrors(text: String): Unit = {
    getFixture.configureByText("dummy.scala", text)
    CodeFoldingManager.getInstance(getProject).buildInitialFoldings(getEditor)

    getFixture.testHighlighting(false, false, false, getFile.getVirtualFile)
  }
}

object ScalaLightCodeInsightFixtureTestAdapter {
  def normalize(text: String, stripTrailingSpaces: Boolean = true): String =
    text.stripMargin.replace("\r", "") match {
      case result if stripTrailingSpaces => result.trim
      case result => result
    }

  def findCaretOffset(text: String, stripTrailingSpaces: Boolean): (String, Int) = {
    val normalized = normalize(text, stripTrailingSpaces)
    (normalized.replace(CARET_MARKER, ""), normalized.indexOf(CARET_MARKER))
  }
}
