package org.jetbrains.plugins.scala.failed.resolve

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.base.libraryLoaders.{PostgresQLLoader, ThirdPartyLibraryLoader}
import org.junit.experimental.categories.Category

/**
  * Created by kate on 4/7/16.
  */

//lots of self type in library, maybe this is cause of problem
@Category(Array(classOf[PerfCycleTests]))
class Postgres extends FailedResolveTest("postgresql") {

  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] = {
    import scala.collection.JavaConversions._
    Seq(PostgresQLLoader()(project, module))
  }

  def testSCL8556(): Unit = doTest()
}
