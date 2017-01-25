package org.jetbrains.plugins.scala.base

import com.intellij.openapi.Disposable
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Disposer
import org.jetbrains.plugins.scala.util.TestUtils.ScalaSdkVersion

/** Special version of library loader whose lifecycle is managed automatically by IDEA disposer mechanism.
  *
  * Loader will be disposed together with the module it's attached to.
  */
class DisposableScalaLibraryLoader(jdk: Sdk)
                                  (implicit override val project: Project,
                                   implicit override val module: Module)
  extends ScalaLibraryLoader(isIncludeReflectLibrary = true) with Disposable {

  private val jdkLoader = JdkLoader(jdk)

  Disposer.register(module, this)

  override def init(implicit version: ScalaSdkVersion): Unit = {
    super.init
    jdkLoader.init
  }

  // libraries are automatically disposed by module
  override def clean(): Unit = {}

  override def dispose(): Unit = clean()
}
