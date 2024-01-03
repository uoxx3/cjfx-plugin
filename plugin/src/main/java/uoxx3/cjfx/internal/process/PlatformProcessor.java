package uoxx3.cjfx.internal.process;

import org.jetbrains.annotations.NotNull;
import ushiosan.jvm.platform.UArchitecture;
import ushiosan.jvm.platform.UPlatform;

/**
 * Class used to resolve the name of the artifacts according to the target platform.
 * <p>
 * Since this is a utility-only class, it is used as a Singleton.
 */
public class PlatformProcessor {
	
	/**
	 * Instance to the current class.
	 */
	private volatile static PlatformProcessor instance;
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * This class cannot be instantiated
	 */
	private PlatformProcessor() {}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Gets the instance of the current class
	 *
	 * @return the {@link PlatformProcessor} instance
	 */
	public static @NotNull PlatformProcessor instance() {
		// Secure access to the class instance
		if (instance == null) {
			synchronized (PlatformProcessor.class) {
				// We need to check the same property twice to determine if the object has been
				// accessed from another thread.
				if (instance == null) {
					instance = new PlatformProcessor();
				}
			}
		}
		
		// Only return the instance already created
		return instance;
	}
	
	/**
	 * Resolves the artifact for selected platform.
	 *
	 * @param platform     The destination platform of the artifact
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact modifier name
	 */
	public @NotNull String resolveArtifactModifier(@NotNull UPlatform platform, @NotNull UArchitecture architecture) {
		switch (platform) {
			case LINUX:
			case SOLARIS:
			case FREE_BSD:
				return resolveArtifactLinuxModifier(architecture);
			case WINDOWS:
				return resolveArtifactWindowsModifier(architecture);
			case MACOS:
				return resolveArtifactMacosModifier(architecture);
			default:
				throw new RuntimeException("Platform not supported");
		}
	}
	
	/* -----------------------------------------------------
	 * Internal methods
	 * ----------------------------------------------------- */
	
	/**
	 * Resolves artifact jar file name depending on configuration
	 *
	 * @param platform     The destination platform of the artifact
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact jar file name
	 */
	public @NotNull String resolveArtifactJar(@NotNull UPlatform platform, @NotNull UArchitecture architecture) {
		switch (platform) {
			case LINUX:
			case SOLARIS:
			case FREE_BSD:
				return resolveArtifactJarLinux(architecture);
			case WINDOWS:
				return resolveArtifactJarWindows(architecture);
			case MACOS:
				return resolveArtifactJarMacos(architecture);
			default:
				throw new RuntimeException("Platform not supported");
		}
	}
	
	/**
	 * Resolves the artifact for linux platforms.
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact modifier name
	 */
	private @NotNull String resolveArtifactLinuxModifier(@NotNull UArchitecture architecture) {
		String format = "linux%s";
		
		// Check macos configuration
		if (architecture == UArchitecture.ARM) {
			format = String.format(format, "-aarch64");
		} else {
			format = String.format(format, "");
		}
		
		return format.trim();
	}
	
	/**
	 * Resolves artifact jar file name depending on configuration
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact jar file name
	 */
	private @NotNull String resolveArtifactJarLinux(@NotNull UArchitecture architecture) {
		String format = "linux%s";
		
		// Check macos configuration
		if (architecture == UArchitecture.ARM) {
			format = String.format(format, "-aarch_64");
		} else {
			format = String.format(format, "-86_64");
		}
		
		return format.trim();
	}
	
	/**
	 * Resolves the artifact for apple platforms.
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact modifier name
	 */
	private @NotNull String resolveArtifactMacosModifier(@NotNull UArchitecture architecture) {
		String format = "mac%s";
		
		// Check macos configuration
		if (architecture == UArchitecture.ARM) {
			format = String.format(format, "-aarch64");
		} else {
			format = String.format(format, "");
		}
		
		return format.trim();
	}
	
	/**
	 * Resolves artifact jar file name depending on configuration
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact jar file name
	 */
	private @NotNull String resolveArtifactJarMacos(@NotNull UArchitecture architecture) {
		String format = "osx%s";
		
		// Check macos configuration
		if (architecture == UArchitecture.ARM) {
			format = String.format(format, "-aarch_64");
		} else {
			format = String.format(format, "-x86_64");
		}
		
		return format.trim();
	}
	
	/**
	 * Resolves the artifact for windows platforms. The architecture
	 * is not used at the moment, but is left for future use.
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact modifier name
	 */
	@SuppressWarnings("unused")
	private @NotNull String resolveArtifactWindowsModifier(@NotNull UArchitecture architecture) {
		return "win";
	}
	
	/**
	 * Resolves artifact jar file name depending on configuration
	 *
	 * @param architecture The destination architecture of the artifact
	 * @return The artifact jar file name
	 */
	@SuppressWarnings("unused")
	private @NotNull String resolveArtifactJarWindows(@NotNull UArchitecture architecture) {
		return "windows-x86_64";
	}
	
}
