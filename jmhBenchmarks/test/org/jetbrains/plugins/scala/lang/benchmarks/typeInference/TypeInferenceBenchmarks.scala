package org.jetbrains.plugins.scala.lang.benchmarks.typeInference

import java.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.scala.base.libraryLoaders._

import scala.collection.JavaConversions._

/**
  * @author Nikolay.Tropin
  */
class AmbigousConversion extends TypeInferenceBenchmarkBase("AmbigousConversion")
class ConstructorPatternComplex extends TypeInferenceBenchmarkBase("ConstructorPatternComplex")
class IncompleteForStatement extends TypeInferenceBenchmarkBase("IncompleteForStatement")
class RawTypes extends TypeInferenceBenchmarkBase("RawTypes")
class Flatten extends TypeInferenceBenchmarkBase("Flatten")
class MapLengthInfix extends TypeInferenceBenchmarkBase("MapLengthInfix")
class VarargTypeInference extends TypeInferenceBenchmarkBase("VarargTypeInference")
class WrongArgumentType extends TypeInferenceBenchmarkBase("WrongArgumentType")
class ShapelessLike extends TypeInferenceBenchmarkBase("ShapelessLike")
class ToArray extends TypeInferenceBenchmarkBase("ToArray")
class UnapplySeqLocalTypeInference extends TypeInferenceBenchmarkBase("UnapplySeqLocalTypeInference")
class UnapplySeqWithImplicitParam extends TypeInferenceBenchmarkBase("UnapplySeqWithImplicitParam")

class SprayRouting extends TypeInferenceBenchmarkBase("SprayRouting") {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] =
    Seq(SprayLoader()(project, module))
}

class Scalaz extends TypeInferenceBenchmarkBase("Scalaz") {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] =
    Seq(ScalaZLoader()(project, module))
}

class Slick extends TypeInferenceBenchmarkBase("Slick") {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] =
    Seq(SlickLoader()(project, module))
}

class Cats extends TypeInferenceBenchmarkBase("Cats") {
  override protected def additionalLibraries(project: Project, module: Module): util.List[ThirdPartyLibraryLoader] =
    Seq(CatsLoader()(project, module))
}
