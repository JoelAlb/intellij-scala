package org.jetbrains.plugins.scala
package base
package libraryLoaders

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.impl.VirtualFilePointerManagerImpl
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager
import com.intellij.testFramework.PsiTestUtil
import org.jetbrains.plugins.scala.project.ModuleExt
import org.jetbrains.plugins.scala.util.TestUtils._

/**
  * @author adkozlov
  */
abstract class ThirdPartyLibraryLoader(implicit val project: Project,
                                       implicit val module: Module)
  extends LibraryLoader {

  protected val name: String

  protected def path(implicit version: ScalaSdkVersion): String

  override def init(implicit version: ScalaSdkVersion): Unit = {
    if (isAlreadyDefined) return

    val path = this.path
    VfsRootAccess.allowRootAccess(path)
    PsiTestUtil.addLibrary(module, path)

    VirtualFilePointerManager.getInstance match {
      case manager: VirtualFilePointerManagerImpl => manager.storePointers()
    }
  }

  private def isAlreadyDefined: Boolean =
    module.libraries.map(_.getName).contains(name)
}

case class ScalaZLoader(implicit override val project: Project,
                        implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "scalaz"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getMockScalazLib(version)
}

case class SlickLoader(implicit override val project: Project,
                       implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "slick"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getMockSlickLib(version)
}

case class SprayLoader(implicit override val project: Project,
                       implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "spray"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getMockSprayLib(version)
}

case class CatsLoader(implicit override val project: Project,
                      implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "cats"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getCatsLib(version)
}

case class Specs2Loader(implicit override val project: Project,
                        implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "specs2"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getSpecs2Lib(version)
}

case class ScalaCheckLoader(implicit override val project: Project,
                            implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "scalacheck"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getScalacheckLib(version)
}

case class PostgresQLLoader(implicit override val project: Project,
                            implicit override val module: Module)
  extends ThirdPartyLibraryLoader {

  override protected val name: String = "postgresql"

  override protected def path(implicit version: ScalaSdkVersion): String =
    getPostgresLib(version)
}
