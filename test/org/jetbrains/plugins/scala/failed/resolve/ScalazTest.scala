package org.jetbrains.plugins.scala.failed.resolve

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.base.libraryLoaders.{ScalaZLoader, ThirdPartyLibraryLoader}
import org.junit.experimental.categories.Category

/**
  * Created by kate on 3/29/16.
  */

@Category(Array(classOf[PerfCycleTests]))
class ScalazTest extends FailedResolveTest("scalaz"){
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] = {
    import scala.collection.JavaConversions._
    Seq(ScalaZLoader()(project, module))
  }

  def testSCL7213(): Unit = doTest()

  def testSCL7227(): Unit = doTest()
}
