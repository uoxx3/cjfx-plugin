package uoxx3.cjfx.internal.resolver.maven.content;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;

import java.util.Arrays;

/**
 * Class with the base elements that the response of a Maven-Central artifact must have.
 */
public class MavenArtifactResponse implements IArtifactResponse {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * The full identifier of the artifact. This usually has the following format:
	 * <pre>{@code
	 * group:artifact:version[:classifier]
	 * }</pre>
	 * Values in square brackets are optional
	 */
	@Expose
	@SerializedName("id")
	protected String id;
	
	/**
	 * The group responsible for publishing the artifact.
	 * This normally has the format of an inverted WEB domain.
	 */
	@Expose
	@SerializedName(value = "g", alternate = {"group"})
	protected String group;
	
	/**
	 * Artifact name. This is the one that contains the asset that you want to use
	 * within Maven projects.
	 */
	@Expose
	@SerializedName(value = "a", alternate = {"artifact"})
	protected String artifact;
	
	/**
	 * The artifact version.
	 */
	@Expose
	@SerializedName(value = "v", alternate = {"version"})
	protected String version;
	
	/**
	 * The type of asset that defines the artifact. Normally in Java projects
	 * these are *.jar files, in Android *.aar and in C++ they can be *.so, *.dll, *.lib, *.o, *.dylib, etc.
	 */
	@Expose
	@SerializedName(value = "p", alternate = {"prototype"})
	protected String prototype;
	
	/**
	 * The possible access classifiers defined within the artifact.
	 * These are not standard and may change depending on the repository.
	 */
	@Expose
	@SerializedName(value = "ec", alternate = {"classifiers"})
	protected String[] classifiers;
	
	/**
	 * The artifact tags to make queries more easily. Like {@link #classifiers()} they are not
	 * standard and change depending on the repository.
	 */
	@Expose
	@SerializedName("tags")
	protected String[] tags;
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * The full identifier of the artifact. This usually has the following format:
	 * <pre>{@code
	 * group:artifact:version[:classifier]
	 * }</pre>
	 * Values in square brackets are optional
	 *
	 * @return The full identifier of the artifact.
	 */
	@Override
	public @NotNull String id() {
		return id;
	}
	
	/**
	 * The group responsible for publishing the artifact.
	 * This normally has the format of an inverted WEB domain.
	 *
	 * @return The group responsible for publishing the artifact
	 */
	@Override
	public @NotNull String group() {
		return group;
	}
	
	/**
	 * Artifact name. This is the one that contains the asset that you want to use
	 * within Maven projects.
	 *
	 * @return The artifact name
	 */
	@Override
	public @NotNull String artifact() {
		return artifact;
	}
	
	/**
	 * The artifact version.
	 *
	 * @return The artifact version
	 */
	@Override
	public @NotNull String version() {
		return version;
	}
	
	/**
	 * The type of asset that defines the artifact. Normally in Java projects
	 * these are *.jar files, in Android *.aar and in C++ they can be *.so, *.dll, *.lib, *.o, *.dylib, etc.
	 *
	 * @return The type of asset that defines the artifact.
	 */
	@Override
	public @NotNull String prototype() {
		return prototype;
	}
	
	/**
	 * The possible access classifiers defined within the artifact.
	 * These are not standard and may change depending on the repository.
	 *
	 * @return The artifact classifiers.
	 */
	@Override
	public String @NotNull [] classifiers() {
		return classifiers;
	}
	
	/**
	 * The artifact tags to make queries more easily. Like {@link #classifiers()} they are not
	 * standard and change depending on the repository.
	 *
	 * @return The artifact tags.
	 */
	@Override
	public String @NotNull [] tags() {
		return tags;
	}
	
	/**
	 * Object string representation
	 *
	 * @return Object string representation
	 */
	@Override
	public String toString() {
		return "MavenArtifactResponse{" +
			   "id='" + id + '\'' +
			   ", group='" + group + '\'' +
			   ", artifact='" + artifact + '\'' +
			   ", version='" + version + '\'' +
			   ", prototype='" + prototype + '\'' +
			   ", classifiers=" + Arrays.toString(classifiers) +
			   ", tags=" + Arrays.toString(tags) +
			   '}';
	}
	
}
