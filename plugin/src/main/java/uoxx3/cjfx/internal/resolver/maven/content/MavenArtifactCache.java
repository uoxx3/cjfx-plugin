package uoxx3.cjfx.internal.resolver.maven.content;

import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;

import java.util.Arrays;

/**
 * Read-only element used as an in-memory cache
 */
public final class MavenArtifactCache extends MavenArtifactResponse {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Special version of the artifact.
	 */
	private final String specialVersion;
	
	/* -----------------------------------------------------
	 * Constructors
	 * ----------------------------------------------------- */
	
	/**
	 * Default cache element constructor.
	 *
	 * @param art     Reference of the artifact to save.
	 * @param special Special version of the artifact.
	 */
	public MavenArtifactCache(@NotNull IArtifactResponse art, @NotNull String special) {
		artifact = art.artifact();
		classifiers = art.classifiers();
		group = art.group();
		id = art.id();
		prototype = art.prototype();
		specialVersion = special;
		tags = art.classifiers();
		version = art.version();
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Gets the special version of the artifact.
	 *
	 * @return The special version of the artifact.
	 */
	public @NotNull String specialVersion() {
		return specialVersion;
	}
	
	/**
	 * Object string representation
	 *
	 * @return Object string representation
	 */
	@Override
	public @NotNull String toString() {
		return "MavenArtifactCache{" +
			   "specialVersion='" + specialVersion + '\'' +
			   ", id='" + id + '\'' +
			   ", group='" + group + '\'' +
			   ", artifact='" + artifact + '\'' +
			   ", version='" + version + '\'' +
			   ", prototype='" + prototype + '\'' +
			   ", classifiers=" + Arrays.toString(classifiers) +
			   ", tags=" + Arrays.toString(tags) +
			   '}';
	}
	
}
