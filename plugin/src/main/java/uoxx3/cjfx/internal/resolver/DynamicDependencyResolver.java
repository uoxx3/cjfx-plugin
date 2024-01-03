package uoxx3.cjfx.internal.resolver;

import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.maven.MavenDependencyResolver;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;
import ushiosan.jvm.collections.USet;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * This class registers several dependency resolvers and performs the action on each of them until
 * one performs the operation correctly.
 * <p>
 * Artifact resolution must be synchronous because Gradle natively does not work with
 * asynchronous elements, and it does not make sense to implement it at the moment.
 * To solve this type of problem, it is necessary to implement an internal cache so as not to make requests
 * every time you want to resolve a dependency.
 */
public class DynamicDependencyResolver extends BaseDependencyResolver {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * List with all registry dependency resolvers
	 */
	private final Set<IDependencyResolver> resolverSet;
	
	/* -----------------------------------------------------
	 * Constructors
	 * ----------------------------------------------------- */
	
	/**
	 * Empty constructor
	 */
	public DynamicDependencyResolver() {
		super(HttpClient.newBuilder()
				  .version(HttpClient.Version.HTTP_2)
				  .connectTimeout(Duration.ofSeconds(10))
				  .executor(Executors.newFixedThreadPool(3))
				  .followRedirects(HttpClient.Redirect.NORMAL)
				  .build(),
			  (new GsonBuilder())
				  .serializeNulls()
				  .setPrettyPrinting()
				  .excludeFieldsWithoutExposeAnnotation()
				  .create());
		// Initialize properties
		resolverSet = USet.make(
			new MavenDependencyResolver(httpClient(), gsonClient()));
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
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
	@Override
	public @NotNull Optional<IArtifactResponse> resolveArtifact(@NotNull String group, @NotNull String artifact,
		@NotNull String version, @NotNull String classifier) {
		// Iterate all resolvers
		for (IDependencyResolver resolver : resolverSet) {
			Optional<IArtifactResponse> response = resolver.resolveArtifact(group, artifact, version, classifier);
			if (response.isPresent()) return response;
		}
		return Optional.empty();
	}
	
	/**
	 * Resolves the required artifact depending on the options provided.
	 *
	 * @param group    The group that is in charge of providing the artifact
	 * @param artifact The name of the artifact
	 * @param version  The version of the artifact. Normally this is a special case, but it can also
	 *                 be a specific version.
	 * @return The response with the artifact information or {@link Optional#empty()} if the artifact was not found.
	 */
	@Override
	public @NotNull Optional<IArtifactResponse> resolveArtifact(@NotNull String group, @NotNull String artifact,
		@NotNull String version) {
		// Iterate all resolvers
		for (IDependencyResolver resolver : resolverSet) {
			Optional<IArtifactResponse> response = resolver.resolveArtifact(group, artifact, version);
			if (response.isPresent()) return response;
		}
		return Optional.empty();
	}
	
}
