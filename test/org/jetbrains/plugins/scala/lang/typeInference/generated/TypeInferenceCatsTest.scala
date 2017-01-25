package org.jetbrains.plugins.scala.lang.typeInference.generated

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.base.libraryLoaders.{CatsLoader, ThirdPartyLibraryLoader}
import org.jetbrains.plugins.scala.lang.typeInference.TypeInferenceTestBase

/**
  * @author Nikolay.Tropin
  */
class TypeInferenceCatsTest extends TypeInferenceTestBase {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] = {
    import scala.collection.JavaConversions._
    Seq(CatsLoader()(project, module))
  }

  override protected def folderPath: String = super.folderPath + "cats/"

  def testSCL10006() = doTest()

//  TODO: this test actually passes in debug IDEA, but failes in tests (ReferenceExpressionResolver.resolve() succeeds
//   in debug idea with the same dependencies, while in tests it returns resolve failure)
//  def testSCL10237() = doTest()

  def testSCL10237_1() = doTest()

  def testSCL10237_2() = doTest()
}
