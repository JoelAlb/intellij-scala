package org.jetbrains.plugins.scala.lang.libraryInjector

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.util.zip.{ZipEntry, ZipOutputStream}

import com.intellij.compiler.CompilerTestUtil
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.impl.VirtualFilePointerManagerImpl
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager
import com.intellij.testFramework.ModuleTestCase
import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.base.libraryLoaders.ThirdPartyLibraryLoader
import org.jetbrains.plugins.scala.base.{JdkLoader, LibraryLoader, ScalaLibraryLoader, SourcesLoader}
import org.jetbrains.plugins.scala.compiler.CompileServerLauncher
import org.jetbrains.plugins.scala.components.libinjection.LibraryInjectorLoader
import org.jetbrains.plugins.scala.debugger.DebuggerTestUtil
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector
import org.jetbrains.plugins.scala.util.ScalaUtil
import org.jetbrains.plugins.scala.util.TestUtils.ScalaSdkVersion
import org.junit.experimental.categories.Category

/**
  * Created by mucianm on 16.03.16.
  */
@Category(Array(classOf[PerfCycleTests]))
class LibraryInjectorTest extends ModuleTestCase {

  val LIBRARY_NAME = "dummy_lib.jar"
  private var libraryLoaders: Seq[LibraryLoader] = Seq.empty

  trait Zippable {
    def zip(toDir: File): File = ???

    def withParent(name: String): Zippable
  }

  case class ZFile(name: String, data: String) extends Zippable {
    override def withParent(parentName: String) = copy(name = s"$parentName/$name")
  }

  case class ZDir(name: String, files: Seq[Zippable]) extends Zippable {
    override def zip(toDir: File): File = {
      val file = new File(toDir, LIBRARY_NAME)
      val zfs = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)))

      def doZip(zipable: Zippable): Unit = {
        zipable match {
          case ZDir(zname, zfiles) =>
            zfs.putNextEntry(new ZipEntry(zname+"/"))
            zfiles.foreach(z=>doZip(z.withParent(zname)))
            zfs.closeEntry()
          case ZFile(zname, zdata) =>
            zfs.putNextEntry(new ZipEntry(zname))
            zfs.write(zdata.getBytes("UTF-8"), 0, zdata.length)
            zfs.closeEntry()
        }
      }
      doZip(this)
      zfs.close()
      file
    }

    override def withParent(parentName: String) = copy(name = s"$parentName/$name")
  }

  override def setUp(): Unit = {
    super.setUp()

    CompilerTestUtil.enableExternalCompiler()
    DebuggerTestUtil.enableCompileServer(true)
    DebuggerTestUtil.forceJdk8ForBuildProcess()
  }

  override def setUpModule(): Unit = {
    super.setUpModule()

    implicit val project = getProject
    implicit val module = getModule
    implicit val version = ScalaSdkVersion._2_11

    libraryLoaders = Seq(ScalaLibraryLoader(isIncludeReflectLibrary = true),
      JdkLoader(getTestProjectJdk),
      SourcesLoader(myProject.getBasePath),
      new ThirdPartyLibraryLoader() {

        override protected val name: String = "injector"

        private val file = testData(getTestName(false))
          .zip(ScalaUtil.createTmpDir("injectorTestLib", ""))

        override protected def path(implicit version: ScalaSdkVersion): String =
          file.getAbsolutePath
      })

    libraryLoaders.foreach(_.init)
  }

  protected override def tearDown() {
    CompilerTestUtil.disableExternalCompiler(myProject)
    CompileServerLauncher.instance.stop()

    libraryLoaders.foreach(_.clean())
    super.tearDown()
  }

  override protected def getTestProjectJdk: Sdk = DebuggerTestUtil.findJdk8()

  val simpleInjector: ZDir = {
    val manifest =
      """
        |<intellij-compat>
        |    <scala-plugin since-version="0.0.0" until-version="9.9.9">
        |        <psi-injector interface="org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector"
        |         implementation="com.foo.bar.Implementation">
        |            <source>META-INF/Implementation.scala</source>
        |            <source>META-INF/Foo.scala</source>
        |        </psi-injector>
        |    </scala-plugin>
        |</intellij-compat>
        |
      """.stripMargin

    val implementationClass =
      """
        |package com.foo.bar
        |import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector
        |
        |class Implementation extends SyntheticMembersInjector { val foo = new Foo }
      """.stripMargin

    val fooClass =
      """
        |package com.foo.bar
        |class Foo
      """.stripMargin

    ZDir("META-INF",
      Seq(
        ZFile(LibraryInjectorLoader.INJECTOR_MANIFEST_NAME, manifest),
        ZFile("Implementation.scala", implementationClass),
        ZFile("Foo.scala", fooClass)
      )
    )
  }

  val testData = Map("Simple" -> simpleInjector)

  def testSimple() {
    VirtualFilePointerManager.getInstance match {
      case manager: VirtualFilePointerManagerImpl => manager.storePointers()
    }
    assert(LibraryInjectorLoader.getInstance(myProject).getInjectorClasses(classOf[SyntheticMembersInjector]).nonEmpty)
  }
}
