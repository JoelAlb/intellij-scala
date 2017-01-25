package org.jetbrains.plugins.scala.failed.resolve

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.base.libraryLoaders.{SlickLoader, ThirdPartyLibraryLoader}
import org.junit.experimental.categories.Category

/**
  * @author Nikolay.Tropin
  */
@Category(Array(classOf[PerfCycleTests]))
class SlickTest extends FailedResolveTest("slick") {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] = {
    import scala.collection.JavaConversions._
    Seq(SlickLoader()(project, module))
  }

  def testSCL8829() = doTest()
}
