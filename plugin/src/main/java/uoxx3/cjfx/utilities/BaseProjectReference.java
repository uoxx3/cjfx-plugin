package uoxx3.cjfx.utilities;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Utility class used for {@link Project} reference retention.
 */
public abstract class BaseProjectReference implements IProjectReference {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Reference to the project where the plugin is applied.
	 * <p>
	 * A weak reference is used because if it refs to {@code null}, then it means that the
	 * project was removed from references within the Gradle core.
	 */
	private final WeakReference<Project> projectRef;
	
	/* -----------------------------------------------------
	 * Constructors
	 * ----------------------------------------------------- */
	
	/**
	 * Default constructor of the current class.
	 *
	 * @param project Project referenced by the class.
	 */
	protected BaseProjectReference(@NotNull Project project) {
		projectRef = new WeakReference<>(project);
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Forces the method to return a valid instance of the project (This means that the instance will never be null).
	 * But if this does not exist then it means that there is no reference to the selected project,
	 * and it will throw an exception.
	 *
	 * @return The project that the object refers to.
	 */
	@Override
	public @NotNull Project requireProject() {
		return getProject().orElseThrow(
			() -> new NullPointerException("There is no reference to the selected project."));
	}
	
	/**
	 * Gets the referenced project object.
	 *
	 * @return The project it refers to or {@link Optional#empty()} if the project was already
	 * 	collected by the GC.
	 */
	@Override
	public @NotNull Optional<Project> getProject() {
		return Optional.ofNullable(projectRef.get());
	}
	
}
