package uoxx3.cjfx.internal.resolver.response;

import org.jetbrains.annotations.NotNull;

/**
 * Interface with the base elements that the response of a Maven artifact must have.
 */
public interface IArtifactResponse {
	
	/**
	 * The full identifier of the artifact. This usually has the following format:
	 * <pre>{@code
	 * group:artifact:version[:classifier]
	 * }</pre>
	 * Values in square brackets are optional
	 *
	 * @return The full identifier of the artifact.
	 */
	@NotNull String id();
	
	/**
	 * The group responsible for publishing the artifact.
	 * This normally has the format of an inverted WEB domain.
	 *
	 * @return The group responsible for publishing the artifact
	 */
	@NotNull String group();
	
	/**
	 * Artifact name. This is the one that contains the asset that you want to use
	 * within Maven projects.
	 *
	 * @return The artifact name
	 */
	@NotNull String artifact();
	
	/**
	 * The artifact version.
	 *
	 * @return The artifact version
	 */
	@NotNull String version();
	
	/**
	 * The type of asset that defines the artifact. Normally in Java projects
	 * these are *.jar files, in Android *.aar and in C++ they can be *.so, *.dll, *.lib, *.o, *.dylib, etc.
	 *
	 * @return The type of asset that defines the artifact.
	 */
	@NotNull String prototype();
	
	/**
	 * The possible access classifiers defined within the artifact.
	 * These are not standard and may change depending on the repository.
	 *
	 * @return The artifact classifiers.
	 */
	String @NotNull [] classifiers();
	
	/**
	 * The artifact tags to make queries more easily. Like {@link #classifiers()} they are not
	 * standard and change depending on the repository.
	 *
	 * @return The artifact tags.
	 */
	String @NotNull [] tags();
	
}
