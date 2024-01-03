package uoxx3.cjfx.internal.resolver.maven.content;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;
import uoxx3.cjfx.internal.resolver.response.IResolverResponse;

/**
 * Class used to determine a common response between dependency resolvers.
 */
public final class MavenResponse implements IResolverResponse {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * The status code of the response. This is not the HTTP response code but rather an
	 * internal state of the repository itself and is for informational purposes only.
	 */
	@Expose
	@SerializedName("status")
	private int status;
	
	/**
	 * Number of artifacts found after the request is completed.
	 */
	@Expose
	@SerializedName("response")
	private MavenContentResponse response;
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * The status code of the response. This is not the HTTP response code but rather an
	 * internal state of the repository itself and is for informational purposes only.
	 *
	 * @return The status code of the response.
	 */
	@Override
	public int status() {
		return status;
	}
	
	/**
	 * Number of artifacts found after the request is completed.
	 *
	 * @return Number of artifacts found.
	 */
	@Override
	public int found() {
		return response.found;
	}
	
	/**
	 * Determine if there are artifacts within the response.
	 *
	 * @return {@code true} if artifacts exist or {@code false} otherwise.
	 * @see #found()
	 */
	@Override
	public IArtifactResponse @NotNull [] artifacts() {
		return response.artifacts;
	}
	
	/**
	 * Internal class that is only used to complete the structure
	 * of a Maven-central response.
	 */
	public static class MavenContentResponse {
		
		/**
		 * Number of artifacts found after the request is completed.
		 */
		@Expose
		@SerializedName(value = "numFound", alternate = {"found"})
		private int found;
		
		/**
		 * Property used to determine the start of artifacts within the collection.
		 */
		@Expose
		@SerializedName("start")
		private int start;
		
		/**
		 * Collection with all the artifacts found.
		 */
		@Expose
		@SerializedName(value = "docs", alternate = {"artifacts", "response", "content"})
		private MavenArtifactResponse[] artifacts;
		
	}
	
}
