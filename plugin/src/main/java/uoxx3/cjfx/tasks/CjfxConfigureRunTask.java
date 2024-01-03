package uoxx3.cjfx.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;
import org.javamodularity.moduleplugin.extensions.RunModuleOptions;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.CjfxModule;
import uoxx3.cjfx.extensions.CjfxConfiguration;
import ushiosan.jvm.collections.USet;
import ushiosan.jvm.filesystem.UResource;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Task that is executed just before the "{@link org.gradle.api.plugins.ApplicationPlugin#TASK_RUN_NAME}" task to configure the
 * JVM arguments so that JavaFX works correctly.
 */
public abstract class CjfxConfigureRunTask extends DefaultTask {
	
	/* -----------------------------------------------------
	 * Constants
	 * ----------------------------------------------------- */
	
	/**
	 * Name of the task within the Gradle context.
	 */
	public static final String TASK_NAME = "configurationRunCjfxTask";
	
	/**
	 * Name of the group where the task is located.
	 */
	public static final String GROUP_NAME = "application";
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Reference to the project to which the task will be applied.
	 */
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Project project;
	
	/**
	 * The current configuration of the plugin.
	 */
	private final CjfxConfiguration configuration;
	
	/**
	 * The task where the Java application is executed with JavaFX.
	 */
	private final JavaExec execTask;
	
	/* -----------------------------------------------------
	 * Constructor
	 * ----------------------------------------------------- */
	
	/**
	 * Task constructor
	 *
	 * @param proj Reference to the project where the task is applied
	 * @param con  Plugin Configuration Reference
	 * @param task Java application execution task
	 */
	@Inject
	public CjfxConfigureRunTask(@NotNull Project proj, @NotNull CjfxConfiguration con, @NotNull JavaExec task) {
		super();
		// Initialize properties
		project = proj;
		configuration = con;
		execTask = task;
		// Configure Tasks
		setGroup(GROUP_NAME);
		execTask.dependsOn(this);
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Method used to configure the JVM arguments to run the Java application along with JavaFX.
	 * <p>
	 * If the {@link org.javamodularity.moduleplugin.ModuleSystemPlugin} plugin is found within the project, only the modules
	 * need to be defined within the project, but if it does not exist, then all JVM arguments are created to determine
	 * the modules and the dependency paths to those modules.
	 */
	@TaskAction
	public void process() {
		// Extract module configurations
		Set<CjfxModule> javafxModules = CjfxModule
			.resolveModuleDependencies(configuration.modules);
		RunModuleOptions runModuleOptions = execTask.getExtensions()
			.findByType(RunModuleOptions.class);
		FileCollection cleanClasspath = extractClasspathWithoutJfx();
		FileCollection jfxClasspath = extractJarClasspathJfx();
		
		// Check if the module options exists
		if (runModuleOptions != null) {
			// Attach the classpath to the current module
			execTask.setClasspath(cleanClasspath.plus(jfxClasspath));
			// Also attach the modules
			javafxModules.forEach(module -> {
				runModuleOptions.getAddModules()
					.add(module.javaModuleName());
			});
		} else {
			// We need to create the classpath from scratch.
			execTask.setClasspath(cleanClasspath);
			
			// Generate JVM configuration
			Set<String> moduleArgs = USet.makeMutable(
				"--module-path",
				jfxClasspath.getAsPath());
			Set<String> jvmArgs = USet.makeMutable(
				"--add-modules",
				javafxModules.stream().map(CjfxModule::javaModuleName)
					.collect(Collectors.joining(",")));
			
			// Try to replace JVM arguments
			List<String> originalJvmArgs = execTask.getJvmArgs();
			if (originalJvmArgs != null) {
				jvmArgs.addAll(originalJvmArgs);
			}
			jvmArgs.addAll(moduleArgs);
			
			// Replace arguments
			execTask.setJvmArgs(jvmArgs);
		}
	}
	
	/* -----------------------------------------------------
	 * Internal methods
	 * ----------------------------------------------------- */
	
	/**
	 * Removes all javafx modules within the JVM arguments classpath. This is done because
	 * when it resolves the arguments automatically, they cause errors and are not configured correctly.
	 *
	 * @return A new classpath but without the JavaFX modules.
	 */
	private @NotNull FileCollection extractClasspathWithoutJfx() {
		FileCollection originalClasspath = execTask.getClasspath();
		Spec<File> action = (File item) -> {
			String basename = UResource.basename(item);
			// Iterate all modules
			return !basename.contains("javafx-");
		};
		
		return originalClasspath.filter(action);
	}
	
	/**
	 * Returns all JavaFX modules in the current classpath. These are configured differently to work
	 * correctly and not cause collisions.
	 *
	 * @return A new classpath whit only the JavaFX modules.
	 */
	private @NotNull FileCollection extractJarClasspathJfx() {
		FileCollection originalClasspath = execTask.getClasspath();
		Spec<File> action = (File item) -> {
			String basename = UResource.basename(item);
			// Iterate all modules
			return basename.contains("javafx-");
		};
		
		return originalClasspath.filter(action);
	}
	
}
