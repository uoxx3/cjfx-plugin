package uoxx3.cjfx.utilities;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Utility interface used for {@link Project} reference retention.
 */
public interface IProjectReference {
	
	/**
	 * Forces the method to return a valid instance of the project (This means that the instance will never be null).
	 * But if this does not exist then it means that there is no reference to the selected project,
	 * and it will throw an exception.
	 *
	 * @return The project that the object refers to.
	 */
	@NotNull Project requireProject();
	
	/**
	 * Gets the referenced project object.
	 *
	 * @return The project it refers to or {@link Optional#empty()} if the project was already
	 * 	collected by the GC.
	 */
	@NotNull Optional<Project> getProject();
	
}
