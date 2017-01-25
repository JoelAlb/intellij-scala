package org.jetbrains.plugins.scala.base

import com.intellij.openapi.actionSystem.IdeActions
import org.jetbrains.plugins.scala.TestFixtureProvider
import org.jetbrains.plugins.scala.extensions.startCommand

/**
  * @author adkozlov
  */
trait EditorActionTestAdapter extends TestFixtureProvider {

  import ScalaLightCodeInsightFixtureTestAdapter.findCaretOffset

  private val stripTrailingSpaces = false

  protected def configureByText(text: String): Unit = {
    val (actual, actualOffset) = findCaretOffset(text, stripTrailingSpaces)

    getFixture.configureByText("dummy.scala", actual)
    getFixture.getEditor.getCaretModel.moveToOffset(actualOffset)
  }

  private def performTest(text: String, expectedText: String)(testBody: () => Unit): Unit = {
    configureByText(text)

    testBody()

    val (expected, _) = findCaretOffset(expectedText, stripTrailingSpaces)
    getFixture.checkResult(expected, stripTrailingSpaces)
  }

  protected def checkGeneratedTextAfterTyping(actual: String, expected: String, charTyped: Char): Unit =
    performTest(actual, expected) { () =>
      typingAction(charTyped)
    }

  protected def typingAction(charTyped: Char): Unit =
    getFixture.`type`(charTyped)

  protected def checkGeneratedTextAfterBackspace(actual: String, expected: String): Unit =
    performTest(actual, expected) { () =>
      backspaceAction()
    }

  protected def backspaceAction(): Unit =
    performEditorAction(IdeActions.ACTION_EDITOR_BACKSPACE)

  protected def checkGeneratedTextAfterEnter(actual: String, expected: String): Unit =
    performTest(actual, expected) { () =>
      enterAction()
    }

  protected def enterAction(): Unit =
    performEditorAction(IdeActions.ACTION_EDITOR_ENTER)

  protected def checkGeneratedTextAfterLiveTemplate(actual: String, expected: String): Unit =
    performTest(actual, expected) { () =>
      liveTemplateAction()
    }

  protected def liveTemplateAction(): Unit =
    performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)

  protected def performEditorAction(action: String): Unit =
    startCommand(getFixture.getProject, new Runnable {
      override def run(): Unit = getFixture.performEditorAction(action)
    }, "")
}

