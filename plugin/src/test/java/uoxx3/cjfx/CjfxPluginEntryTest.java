/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package uoxx3.cjfx;

import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import uoxx3.cjfx.extensions.CjfxConfiguration;
import uoxx3.cjfx.tasks.CjfxConfigureRunTask;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A simple unit test for the 'uoxx3.cjfx.greeting' plugin.
 */
class CjfxPluginEntryTest {
	
	@Test
	void pluginRegistersAExtensionTest() {
		// Create a test project and apply the plugin
		Project project = ProjectBuilder.builder()
			.withName("pluginRegistersAExtensionTest")
			.build();
		project.getPlugins().apply("io.github.uoxx3.cjfx");
		
		// Verify the result
		assertNotNull(project.getExtensions()
						  .findByType(CjfxConfiguration.class));
	}
	
	@Test
	void pluginRegistersATaskTest() {
		// Create a test project and apply the plugin
		Project project = ProjectBuilder.builder()
			.withName("pluginRegistersATaskTest")
			.build();
		project.getPlugins().apply(ApplicationPlugin.APPLICATION_PLUGIN_NAME);
		project.getPlugins().apply("io.github.uoxx3.cjfx");
		
		// Verify the result
		assertNotNull(project.getTasks()
						  .findByName(CjfxConfigureRunTask.TASK_NAME));
	}
	
}
