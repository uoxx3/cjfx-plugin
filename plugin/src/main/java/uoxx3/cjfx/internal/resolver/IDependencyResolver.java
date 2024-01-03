package uoxx3.cjfx.internal.resolver;

import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;
import ushiosan.jvm.UObject;

import java.util.Optional;

/**
 * Interface used to resolve dependencies on artifacts required by the user.
 * <p>
 * Artifact resolution must be synchronous because Gradle natively does not work with
 * asynchronous elements, and it does not make sense to implement it at the moment.
 * To solve this type of problem, it is necessary to implement an internal cache so as not to make requests
 * every time you want to resolve a dependency.
 */
public interface IDependencyResolver {
	
	/**
	 * Verify that the specified version is a special case.
	 *
	 * @param version The content you want to verify
	 * @return {@code true} if this is a special case or {@code false} otherwise
	 */
	boolean isSpecialCase(@NotNull String version);
	
	/**
	 * Verify that the specified version is a special case.
	 *
	 * @param version The content you want to verify
	 * @return {@code true} if this is a special case or {@code false} otherwise
	 */
	default boolean isSpecialCase(@NotNull ObservableValue<String> version) {
		String content = version.getValue();
		return isSpecialCase(UObject.notNull(content, ""));
	}
	
	/**
	 * Extracts a special case from the specified version
	 *
	 * @param version The content you want to verify
	 * @return The excerpt from the special version or an empty text if no special case is found.
	 * @see #isSpecialCase(String)
	 */
	@NotNull String extractCase(@NotNull String version);
	
	/**
	 * Extracts a special case from the specified version
	 *
	 * @param version The content you want to verify
	 * @return The excerpt from the special version or an empty text if no special case is found.
	 * @see #isSpecialCase(String)
	 */
	default @NotNull String extractCase(@NotNull ObservableValue<String> version) {
		String content = version.getValue();
		return extractCase(UObject.notNull(content, ""));
	}
	
	/**
	 * Resolves the required artifact depending on the options provided.
	 *
	 * @param group      The group that is in charge of providing the artifact
	 * @param artifact   The name of the artifact
	 * @param version    The version of the artifact. Normally this is a special case, but it can also
	 *                   be a specific version.
	 * @param classifier The Artifact Classifier
	 * @return The response with the artifact information or {@link Optional#empty()} if the artifact was not found.
	 */
	@NotNull Optional<IArtifactResponse> resolveArtifact(
		@NotNull String group,
		@NotNull String artifact,
		@NotNull String version,
		@NotNull String classifier);
	
	/**
	 * Resolves the required artifact depending on the options provided.
	 *
	 * @param group    The group that is in charge of providing the artifact
	 * @param artifact The name of the artifact
	 * @param version  The version of the artifact. Normally this is a special case, but it can also
	 *                 be a specific version.
	 * @return The response with the artifact information or {@link Optional#empty()} if the artifact was not found.
	 */
	@NotNull Optional<IArtifactResponse> resolveArtifact(
		@NotNull String group,
		@NotNull String artifact,
		@NotNull String version);
	
}
