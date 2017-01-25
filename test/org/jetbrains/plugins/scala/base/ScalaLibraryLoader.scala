package org.jetbrains.plugins.scala
package base

import java.io.File

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.{JavaSdk, Sdk}
import com.intellij.openapi.roots._
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.impl.VirtualFilePointerManagerImpl
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager
import com.intellij.openapi.vfs.{JarFileSystem, LocalFileSystem, VirtualFile}
import com.intellij.testFramework.PsiTestUtil
import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.synthetic.SyntheticClasses
import org.jetbrains.plugins.scala.project._
import org.jetbrains.plugins.scala.project.template.Artifact
import org.jetbrains.plugins.scala.util.TestUtils._

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  * Nikolay.Tropin
  * 5/29/13
  */
trait LibraryLoader {
  implicit val project: Project
  implicit val module: Module

  def init(implicit version: ScalaSdkVersion): Unit

  def clean(): Unit = {}

  protected def modifiableRootModel: ModifiableRootModel =
    ModuleRootManager.getInstance(module).getModifiableModel
}

case class JdkLoader(private val jdk: Sdk)
                    (implicit val project: Project, val module: Module)
  extends LibraryLoader {

  override def init(implicit version: ScalaSdkVersion): Unit = {
    val rootModel = modifiableRootModel
    rootModel.setSdk(jdk)
    inWriteAction {
      rootModel.commit()
    }
  }
}

object JdkLoader {

  def mock(implicit project: Project, module: Module): JdkLoader = {
    val mockJdk = JavaSdk.getInstance.createJdk("Java SDK", getDefaultJdk, false)
    JdkLoader(mockJdk)
  }
}

case class SourcesLoader(private val path: String)
                        (implicit val project: Project, val module: Module)
  extends LibraryLoader {

  import FileUtil.createIfDoesntExist
  import PsiTestUtil.{addSourceRoot, removeSourceRoot}

  override def init(implicit version: ScalaSdkVersion): Unit = {
    createIfDoesntExist(new File(path))
    addSourceRoot(module, rootFile)
  }

  override def clean(): Unit =
    removeSourceRoot(module, rootFile)

  private def rootFile: VirtualFile =
    LocalFileSystem.getInstance.refreshAndFindFileByPath(path)
}

case class ScalaLibraryLoader(isIncludeReflectLibrary: Boolean = false)
                             (implicit val project: Project, val module: Module)
  extends LibraryLoader {

  private val addedLibraries = ArrayBuffer[Library]()

  override def init(implicit version: ScalaSdkVersion): Unit = {
    ScalaLoader.loadScala()

    addSyntheticClasses()

    addScalaSdk(version)
  }

  private def addSyntheticClasses(): Unit =
    project.getComponent(classOf[SyntheticClasses]) match {
      case classes if !classes.isClassesRegistered => classes.registerClasses()
      case _ =>
    }

  override def clean(): Unit =
    inWriteAction {
      addedLibraries.foreach(module.detach)
    }

  private def addScalaSdk(sdkVersion: ScalaSdkVersion) = {
    val compilerPath = getScalaCompilerPath(sdkVersion)
    val libraryPath = getScalaLibraryPath(sdkVersion)
    val reflectPath = getScalaReflectPath(sdkVersion)

    val scalaSdkJars = Seq(libraryPath, compilerPath) ++ (if (isIncludeReflectLibrary) Seq(reflectPath) else Seq.empty)
    val classRoots = scalaSdkJars.map(path => JarFileSystem.getInstance.refreshAndFindFileByPath(path + "!/")).asJava

    val scalaLibrarySrc = getScalaLibrarySrc(sdkVersion)
    val srcsRoots = Seq(JarFileSystem.getInstance.refreshAndFindFileByPath(scalaLibrarySrc + "!/")).asJava
    val scalaSdkLib = PsiTestUtil.addProjectLibrary(module, "scala-sdk", classRoots, srcsRoots)
    val languageLevel = Artifact.ScalaCompiler.versionOf(new File(compilerPath))
      .flatMap(ScalaLanguageLevel.from).getOrElse(ScalaLanguageLevel.Default)

    inWriteAction {
      scalaSdkLib.convertToScalaSdkWith(languageLevel, scalaSdkJars.map(new File(_)))
      module.attach(scalaSdkLib)
      addedLibraries += scalaSdkLib
    }

    VirtualFilePointerManager.getInstance match {
      case manager: VirtualFilePointerManagerImpl => manager.storePointers()
    }
  }
}
