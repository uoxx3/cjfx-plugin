package uoxx3.cjfx.internal.resolver.maven;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.internal.resolver.BaseDependencyResolver;
import uoxx3.cjfx.internal.resolver.maven.content.MavenArtifactCache;
import uoxx3.cjfx.internal.resolver.maven.content.MavenResponse;
import uoxx3.cjfx.internal.resolver.response.IArtifactResponse;
import uoxx3.cjfx.internal.resolver.response.IResolverResponse;
import ushiosan.jvm.collections.USet;
import ushiosan.jvm.content.UPair;
import ushiosan.jvm.http.UHttpRequest;
import ushiosan.jvm.http.UHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface used to resolve dependencies on artifacts required by the user.
 * <p>
 * Artifact resolution must be synchronous because Gradle natively does not work with
 * asynchronous elements, and it does not make sense to implement it at the moment.
 * To solve this type of problem, it is necessary to implement an internal cache so as not to make requests
 * every time you want to resolve a dependency.
 */
public class MavenDependencyResolver extends BaseDependencyResolver {
	
	/* -----------------------------------------------------
	 * Constants
	 * ----------------------------------------------------- */
	
	/**
	 * URL where all HTTP requests will be made
	 */
	private static final String MAVEN_API_URL =
		"https://search.maven.org/solrsearch/select?q=%s+&core=gav&rows=10&wt=json";
	
	/**
	 * Query format for group specification within the API
	 */
	private static final String GROUP_QUERY = "g:%s";
	
	/**
	 * Query format for artifact specification within the API
	 */
	private static final String ARTIFACT_QUERY = "+AND+a:%s";
	
	/**
	 * Query format for version specification within the API
	 */
	private static final String VERSION_QUERY = "+AND+v:%s";
	
	/**
	 * Query format for classifier specification within the API
	 */
	private static final String CLASSIFIER_QUERY = "+AND+l:%s";
	
	/**
	 * Query format for packaging specification within the API
	 */
	@SuppressWarnings("unused")
	private static final String PACKAGING_QUERY = "+AND+p:%s";
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Collection with in-memory cache of already resolved artifacts.
	 */
	private final Set<SoftReference<MavenArtifactCache>> artifactMemoryCache;
	
	/* -----------------------------------------------------
	 * Constructor
	 * ----------------------------------------------------- */
	
	/**
	 * Default constructor with the necessary instances.
	 *
	 * @param client HTTP client instance
	 * @param gson   Gson instance
	 */
	public MavenDependencyResolver(@NotNull HttpClient client, @NotNull Gson gson) {
		super(client, gson);
		// Initialize properties
		artifactMemoryCache = USet.makeMutable();
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
		// Check if the version already exists in the cache
		boolean isSpecialCase = isSpecialCase(version);
		UPair<Boolean, IArtifactResponse> cacheFound = isInCache(isSpecialCase ? extractCase(version) : version);
		if (cacheFound.first) return Optional.ofNullable(cacheFound.second);
		
		// Generate query url
		String format = isSpecialCase ?
						generateQuery(CLASSIFIER_QUERY) :
						generateQuery(VERSION_QUERY, CLASSIFIER_QUERY);
		String mavenQuery = isSpecialCase ?
							String.format(format, group, artifact, version, classifier) :
							String.format(format, group, artifact, classifier);
		return resolveArtifactImpl(mavenQuery, extractCase(version));
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
		// Check if the version already exists in the cache
		boolean isSpecialCase = isSpecialCase(version);
		UPair<Boolean, IArtifactResponse> cacheFound = isInCache(isSpecialCase ? extractCase(version) : version);
		if (cacheFound.first) return Optional.ofNullable(cacheFound.second);
		
		// Generate query url
		String format = isSpecialCase ?
						generateQuery() :
						generateQuery(VERSION_QUERY);
		String mavenQuery = isSpecialCase ?
							String.format(format, group, artifact) :
							String.format(format, group, artifact, version);
		return resolveArtifactImpl(mavenQuery, extractCase(version));
	}
	
	/* -----------------------------------------------------
	 * Internal methods
	 * ----------------------------------------------------- */
	
	/**
	 * Resolves the required artifact depending on the options provided.
	 *
	 * @param query       The complete query of the HTTP request
	 * @param specialCase The name of the special case or empty text if no such special case exists.
	 * @return The response with the artifact information or {@link Optional#empty()} if the artifact was not found.
	 */
	private @NotNull Optional<IArtifactResponse> resolveArtifactImpl(@NotNull String query, @NotNull String specialCase) {
		try {
			// Generate request instance
			HttpRequest mavenRequest = UHttpRequest.makeRequest(query)
				.GET()
				.build();
			HttpResponse<InputStream> mavenResponse = httpClient()
				.send(mavenRequest, HttpResponse.BodyHandlers.ofInputStream());
			
			// Check if response is valid
			Optional<UPair<Integer, String>> responseErrorOpt = UHttpResponse.detectError(mavenResponse);
			if (responseErrorOpt.isPresent()) {
				throw new IOException(String.format("%d - %s", responseErrorOpt.get().first,
													responseErrorOpt.get().second));
			}
			
			// Parse http request to a valid json information
			IResolverResponse response;
			try (Reader responseReader = new InputStreamReader(mavenResponse.body())) {
				response = gsonClient().fromJson(responseReader, MavenResponse.class);
			}
			
			// Check if any artifact found
			if (!response.hasArtifacts()) throw new IOException("No artifacts found");
			Optional<IArtifactResponse> artifactFound;
			
			// If the special case is not defined
			if (specialCase.isBlank()) return Arrays.stream(response.artifacts()).findFirst();
			
			// Check every special case
			switch (specialCase.trim()) {
				case "latest":
					Pattern latestPattern = Pattern.compile("^(\\d+\\.)*(\\*|\\d+)$");
					artifactFound = Arrays.stream(response.artifacts())
						.filter(artifact -> {
							Matcher matcher = latestPattern.matcher(artifact.version());
							return matcher.find();
						}).findFirst();
					break;
				case "early":
					Pattern earlyPattern = Pattern.compile("^(\\d+\\.)*(\\d+-ea\\+\\d+)$");
					artifactFound = Arrays.stream(response.artifacts())
						.filter(artifact -> {
							Matcher matcher = earlyPattern.matcher(artifact.version());
							return matcher.find();
						}).findFirst();
					break;
				default:
					Pattern customPattern = Pattern.compile(specialCase);
					artifactFound = Arrays.stream(response.artifacts())
						.filter(artifact -> {
							Matcher matcher = customPattern.matcher(artifact.version());
							return matcher.find();
						}).findFirst();
					break;
			}
			// Check if artifact found
			if (artifactFound.isEmpty()) return Optional.empty();
			MavenArtifactCache cacheItem = new MavenArtifactCache(artifactFound.get(), specialCase);
			
			// Insert the element in the cache
			artifactMemoryCache.add(new SoftReference<>(cacheItem));
			System.out.printf("> cjfx-version-resolved: %s -> %s%n",
							  cacheItem.specialVersion(),
							  cacheItem.version());
			
			return artifactFound;
		} catch (Exception e) {
			System.err.printf("Error in http request: %s%n", e.getMessage());
		}
		return Optional.empty();
	}
	
	/**
	 * Determines if the special case of the version is found within the cache.
	 *
	 * @param version Version with special case
	 * @return Returns a pair of values where the first determines whether the version is found in
	 * 	cache and the second determines the object found in cache.
	 * 	If the first element is {@code false} then the second is automatically {@code null}.
	 */
	private @NotNull UPair<Boolean, IArtifactResponse> isInCache(@NotNull String version) {
		// Generate set iterator
		Iterator<SoftReference<MavenArtifactCache>> iterator = artifactMemoryCache.iterator();
		
		// Iterate all elements
		while (iterator.hasNext()) {
			SoftReference<MavenArtifactCache> reference = iterator.next();
			MavenArtifactCache cacheItem = reference.get();
			
			// Delete all invalid elements
			if (cacheItem == null) {
				iterator.remove();
				continue;
			}
			
			// Check if the version exists
			if (cacheItem.specialVersion().equals(version)) {
				return UPair.make(true, cacheItem);
			}
		}
		
		return UPair.make(false, null);
	}
	
	/**
	 * Generate a new URL with the necessary options to query the API.
	 *
	 * @param queries The queries that you want to make.
	 * @return A new URL with the necessary options to query the API.
	 */
	private @NotNull String generateQuery(String @NotNull ... queries) {
		// Generate query information
		String baseQuery = String.join("", GROUP_QUERY, ARTIFACT_QUERY);
		String complementQuery = String.join("", queries);
		
		// Generate url result
		String fullQuery = baseQuery + complementQuery;
		return String.format(MAVEN_API_URL, fullQuery);
	}
	
}
