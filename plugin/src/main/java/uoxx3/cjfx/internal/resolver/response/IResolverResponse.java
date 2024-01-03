package uoxx3.cjfx.internal.resolver.response;

import org.jetbrains.annotations.NotNull;

/**
 * Interface used to determine a common response between dependency resolvers.
 */
public interface IResolverResponse {
	
	/**
	 * The status code of the response. This is not the HTTP response code but rather an
	 * internal state of the repository itself and is for informational purposes only.
	 *
	 * @return The status code of the response.
	 */
	int status();
	
	/**
	 * Number of artifacts found after the request is completed.
	 *
	 * @return Number of artifacts found.
	 */
	int found();
	
	/**
	 * Determine if there are artifacts within the response.
	 *
	 * @return {@code true} if artifacts exist or {@code false} otherwise.
	 * @see #found()
	 */
	default boolean hasArtifacts() {
		return found() > 0;
	}
	
	/**
	 * Collection with all the artifacts found.
	 *
	 * @return All artifacts found.
	 * @see #hasArtifacts()
	 */
	IArtifactResponse @NotNull [] artifacts();
	
}
