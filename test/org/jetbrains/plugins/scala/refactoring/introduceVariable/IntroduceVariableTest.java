package org.jetbrains.plugins.scala.refactoring.introduceVariable;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.scala.base.JdkLoader$;
import org.jetbrains.plugins.scala.base.LibraryLoader;
import org.jetbrains.plugins.scala.base.ScalaLibraryLoader;
import org.jetbrains.plugins.scala.util.TestUtils;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * @author Alexander Podkhalyuzin
 */
@RunWith(AllTests.class)
public class IntroduceVariableTest extends AbstractIntroduceVariableTestBase {
  @NonNls
  private static final String DATA_PATH = "/introduceVariable/data";

  private LibraryLoader[] myLibraryLoaders = new LibraryLoader[2];

  public IntroduceVariableTest() {
    super(TestUtils.getTestDataPath() + DATA_PATH);
  }

  public static Test suite() {
    return new IntroduceVariableTest();
  }

  @Override
  protected void setUp(Project project) {
    super.setUp(project);
    Module module = ModuleManager.getInstance(project).getModules()[0];

    myLibraryLoaders[0] = new ScalaLibraryLoader(false, project, module);
    myLibraryLoaders[1] = JdkLoader$.MODULE$.mock(project, module);

    for (LibraryLoader loader : myLibraryLoaders) {
      loader.init(TestUtils.DEFAULT_SCALA_SDK_VERSION);
    }
  }

  public void tearDown() throws Exception {
    for (LibraryLoader loader : myLibraryLoaders) {
      loader.clean();
    }
  }
}
