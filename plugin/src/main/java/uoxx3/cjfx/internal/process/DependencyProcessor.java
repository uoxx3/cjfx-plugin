package uoxx3.cjfx.internal.process;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.CjfxModule;
import uoxx3.cjfx.extensions.CjfxConfiguration;
import uoxx3.cjfx.internal.resolver.DynamicDependencyResolver;
import uoxx3.cjfx.internal.resolver.IDependencyResolver;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;
import uoxx3.cjfx.utilities.BaseProjectReference;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class that performs operations such as updating and resolving
 * dependencies on JavaFX modules.
 */
public final class DependencyProcessor extends BaseProjectReference {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Object in charge of resolving the versions of the JavaFX modules.
	 */
	private final IDependencyResolver resolver;
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Default constructor of the current class.
	 *
	 * @param project Project referenced by the class.
	 */
	public DependencyProcessor(@NotNull Project project) {
		super(project);
		resolver = new DynamicDependencyResolver();
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Updates the project dependencies based on the new configuration.
	 * <p>
	 * Unfortunately any changes made cause this method to have to do the entire process and there
	 * is no way to update the dependencies without having to change everything again.
	 *
	 * @param con The current configuration of the plugin
	 */
	public void updateDependencies(@NotNull CjfxConfiguration con) {
		// Remove all old dependencies
		requireProject().getConfigurations()
			.forEach(pCon -> removeOldDependencies(pCon, con));
		
		// Check if new modules exists
		if (con.modules.isEmpty()) return;
		
		// Variables used to resolve dependencies
		Set<CjfxModule> destinationModules = CjfxModule.resolveModuleDependencies(con.modules);
		String modifier = PlatformProcessor.instance().resolveArtifactModifier(
			con.observablePlatform().getValue(),
			con.observableArchitecture().getValue());
		String targetVersion = con.observableVersion().getValue();
		
		// Check if the version contains a special case
		if (resolver.isSpecialCase(con.observableVersion())) {
			// Only resolve the first module version
			CjfxModule moduleTest = destinationModules.stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No modules found"));
			IArtifactResponse response = resolver.resolveArtifact(
					"org.openjfx",
					moduleTest.artifactName(), targetVersion)
				.orElseThrow(() -> new RuntimeException("Cannot resolve artifact version"));
			
			// Replace the target version
			targetVersion = response.version();
		}
		
		// Resolve all dependencies
		final String finalTargetVersion = targetVersion;
		List<String> moduleDependencies = destinationModules.stream()
			.map(module -> module.artifactGroup(finalTargetVersion, modifier))
			.collect(Collectors.toList());
		
		// Iterate all project configurations
		for (String projectConfiguration : con.dependencyConfigurations) {
			Configuration artifactConfiguration = requireProject().getConfigurations()
				.findByName(projectConfiguration);
			
			// Iterate all dependencies
			if (artifactConfiguration == null) continue;
			for (String dependency : moduleDependencies) {
				requireProject().getDependencies()
					.add(projectConfiguration, dependency);
			}
		}
	}
	
	/* -----------------------------------------------------
	 * Internal methods
	 * ----------------------------------------------------- */
	
	/**
	 * Remove dependencies on old configurations
	 *
	 * @param moduleCon The configuration specifies what to delete
	 * @param con       The plugin configuration
	 */
	private void removeOldDependencies(@NotNull Configuration moduleCon, @NotNull CjfxConfiguration con) {
		// Check if the configuration exists in the current configuration
		if (con.dependencyConfigurations.contains(moduleCon.getName())) return;
		
		// Remove all javafx dependencies from another configurations
		moduleCon.getDependencies().removeIf(dependency -> {
			// Check if dependency group is valid
			if (dependency.getGroup() == null) return false;
			// Remove dependency information
			String group = dependency.getGroup();
			return group.contentEquals("org.openjfx");
		});
	}
	
}
