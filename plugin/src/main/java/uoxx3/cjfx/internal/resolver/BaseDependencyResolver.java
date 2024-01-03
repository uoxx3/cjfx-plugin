package uoxx3.cjfx.internal.resolver;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base structure for creating dependency resolving classes.
 */
public abstract class BaseDependencyResolver implements IDependencyResolver {
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Regular expression used to check if artifact special notation is valid
	 */
	protected static final Pattern CONFIGURATION_CHECKER =
		Pattern.compile("#(.+)#");
	
	/**
	 * HTTP client configured to make requests to a Maven repository API.
	 */
	private final HttpClient httpClient;
	
	/**
	 * Gson instance for serialization of Json objects
	 */
	private final Gson gsonClient;
	
	/* -----------------------------------------------------
	 * Constructor
	 * ----------------------------------------------------- */
	
	/**
	 * Default constructor with the necessary instances.
	 *
	 * @param client HTTP client instance
	 * @param gson   Gson instance
	 */
	public BaseDependencyResolver(@NotNull HttpClient client, @NotNull Gson gson) {
		httpClient = client;
		gsonClient = gson;
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Verify that the specified version is a special case.
	 *
	 * @param version The content you want to verify
	 * @return {@code true} if this is a special case or {@code false} otherwise
	 */
	@Override
	public boolean isSpecialCase(@NotNull String version) {
		Matcher matcher = CONFIGURATION_CHECKER.matcher(version);
		return matcher.find();
	}
	
	/**
	 * Extracts a special case from the specified version
	 *
	 * @param version The content you want to verify
	 * @return The excerpt from the special version or an empty text if no special case is found.
	 * @see #isSpecialCase(String)
	 */
	public @NotNull String extractCase(@NotNull String version) {
		Matcher matcher = CONFIGURATION_CHECKER.matcher(version);
		if (!matcher.find()) return "";
		
		return matcher.group(1).trim();
	}
	
	/**
	 * Access to the instance of object {@link HttpClient}
	 *
	 * @return The {@link HttpClient} instance
	 */
	public @NotNull HttpClient httpClient() {
		return httpClient;
	}
	
	/**
	 * Access to the instance of object {@link Gson}
	 *
	 * @return The {@link Gson} instance
	 */
	public @NotNull Gson gsonClient() {
		return gsonClient;
	}
	
}
