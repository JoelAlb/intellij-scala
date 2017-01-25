package org.jetbrains.plugins.scala
package lang.adjustTypes.tests

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.base.libraryLoaders.{CatsLoader, ThirdPartyLibraryLoader}
import org.jetbrains.plugins.scala.lang.adjustTypes.AdjustTypesTestBase

/**
 * Nikolay.Tropin
 * 7/11/13
 */
class AdjustTypesTests extends AdjustTypesTestBase {

  def testSimpleJava() = doTest()

  def testParameterizedJava() = doTest()

  def testJavaWithImportAlias() = doTest()

  def testJavaInnerClasses() = doTest()

  def testPackagings() = doTest()

  def testAmbiguity() = doTest()

  def testSeveralLevelImports() = doTest()

  def testTypeAlias() = doTest()

  def testTypeProjection() = doTest()

  def testThisType() = doTest()

  def testPrefixed() = doTest()

  def testImportedInnerClass() = doTest()

  def testInheritedTypeInObject() = doTest()
}

class AdjustCatsTypeTest extends AdjustTypesTestBase {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] = {
    import scala.collection.JavaConversions._
    Seq(CatsLoader()(project, module))
  }

  def testSCL10006() = doTest()
}
