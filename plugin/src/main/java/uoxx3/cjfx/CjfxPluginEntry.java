package uoxx3.cjfx;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.JavaExec;
import org.javamodularity.moduleplugin.ModuleSystemPlugin;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.extensions.CjfxConfiguration;
import uoxx3.cjfx.internal.process.DependencyProcessor;
import uoxx3.cjfx.tasks.CjfxConfigureRunTask;

/**
 * Class that represents the functionality of the plugin.
 * <p>
 * Here the extensions and tasks to be performed within the plugin are defined.
 */
public class CjfxPluginEntry implements Plugin<Project> {
	
	/**
	 * Apply this plugin to the given target object.
	 *
	 * @param project The target project
	 */
	public void apply(@NotNull Project project) {
		// We register the necessary plugins for JavaFX to work correctly and do not
		// need to be registered again.
		project.getPlugins().apply(JavaPlugin.class);
		project.getPlugins().apply(ModuleSystemPlugin.class);
		
		// Local variables with the instances of the objects that will be used
		// throughout the life cycle of the plugin.
		DependencyProcessor dependencyProcessor = new DependencyProcessor(project);
		CjfxConfiguration configuration = project.getExtensions()
			.create(CjfxConfiguration.EXTENSION_NAME, CjfxConfiguration.class, project, dependencyProcessor);
		
		// The executable settings will only be applied if the project has the
		// "ApplicationPlugin" plugin. Otherwise, such configuration is not necessary.
		if (!project.getPlugins().hasPlugin(ApplicationPlugin.class)) return;
		
		// We get the execution task of the Java application. This task is used
		// to configure the JavaFX classpath as well as its modules.
		JavaExec javaRunTask = (JavaExec) project.getTasks()
			.getByName(ApplicationPlugin.TASK_RUN_NAME);
		
		// We create the task that configures the modules and the JVM classpath for
		// the application to work.
		project.getTasks().create(CjfxConfigureRunTask.TASK_NAME, CjfxConfigureRunTask.class,
								  project, configuration, javaRunTask);
	}
	
}
