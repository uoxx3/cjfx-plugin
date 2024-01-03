package uoxx3.cjfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import ushiosan.jvm.collections.UList;
import ushiosan.jvm.collections.USet;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * Listed with possible available JavaFX modules.
 * <p>
 * Only one option is not a module but a special type that pretends to be a module called
 * {@link CjfxModule#ALL} that contains all the JavaFX modules and is syntactic sugar so as not to
 * have to define all the elements.
 */
public enum CjfxModule {
	/**
	 * Only the base of content from JavaFX
	 */
	BASE,
	/**
	 * Module with all graphics functionality
	 */
	GRAPHICS(BASE),
	/**
	 * Module with all GUI and functionality
	 */
	CONTROLS(BASE, GRAPHICS),
	/**
	 * Module with all functionality to use MVC in the project
	 */
	FXML(BASE, GRAPHICS),
	/**
	 * Module with all images, videos, audio functionality
	 */
	MEDIA(BASE, GRAPHICS),
	/**
	 * Module with functionality to work with JavaSwing and JavaFX
	 */
	SWING(BASE, GRAPHICS),
	/**
	 * Module with all web functionality
	 */
	WEB(BASE, CONTROLS, MEDIA),
	/**
	 * All modules
	 */
	ALL(BASE, GRAPHICS, CONTROLS, FXML, MEDIA, SWING, WEB);
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Array with all the modules needed for the current module.
	 */
	private final CjfxModule @NotNull [] dependencies;
	
	/* -----------------------------------------------------
	 * Constructors
	 * ----------------------------------------------------- */
	
	/**
	 * Constructor for enumerated type for JavaFX modules
	 *
	 * @param modules Dependencies of the current module. It can be empty if necessary.
	 */
	CjfxModule(CjfxModule @NotNull ... modules) {
		dependencies = modules;
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Check the dependencies according to the selected modules. These modules will be
	 * used to determine the dependencies and should not be repeated, in addition to
	 * the {@link CjfxModule#ALL} element being a special option to represent the whole set.
	 *
	 * @param collection User-defined modules.
	 * @return Set of resolved modules for dependencies
	 */
	public static @NotNull @Unmodifiable Set<CjfxModule> resolveModuleDependencies(@NotNull Collection<CjfxModule> collection) {
		Set<CjfxModule> resultDependencies = USet.makeMutable();
		
		for (var module : collection) {
			// ALL option contains all modules
			if (module == CjfxModule.ALL) {
				resultDependencies.clear();
				resultDependencies.addAll(UList.make(module.dependencies()));
				break;
			}
			resultDependencies.add(module);
			resultDependencies.addAll(UList.make(module.dependencies()));
		}
		
		return Collections.unmodifiableSet(resultDependencies);
	}
	
	/**
	 * Gets all dependencies of the current module.
	 * <p>
	 * Do not confuse with resolving the modules, since this only returns the dependencies and does not
	 * take into account the dependencies of the modules defined within the dependencies themselves.
	 * In this case use method {@link CjfxModule#resolveModuleDependencies(Collection)}.
	 *
	 * @return An array with the dependencies of the current module
	 */
	public CjfxModule @NotNull [] dependencies() {
		return dependencies;
	}
	
	/**
	 * Gets the name of the javaFX module formatted for Java 9 modules.
	 * The only option that is not formatted is the {@link #ALL} element because it is a special option.
	 *
	 * @return The Java 9 format module name of the current element.
	 */
	public @NotNull String javaModuleName() {
		if (this == ALL) return "";
		String format = "javafx.%s";
		String moduleName = name().toLowerCase(Locale.ROOT);
		
		// Resolve module name
		return String.format(format, moduleName);
	}
	
	/**
	 * Gets the name of the gradle artifact of the current module.
	 * <p>
	 * Like the {@link #javaModuleName()} method, the {@link #ALL} option does not return anything
	 * because it is a special option.
	 *
	 * @return The name of the gradle artifact
	 */
	public @NotNull String artifactName() {
		if (this == ALL) return "";
		String format = "javafx-%s";
		String moduleName = name().toLowerCase(Locale.ROOT);
		
		// Resolve artifact name
		return String.format(format, moduleName);
	}
	
	/**
	 * Gets the name of the artifact along with the publisher group of the artifact.
	 * Ideal for managing gradle dependencies.
	 *
	 * @return Name of the artifact along with the publisher group.
	 */
	public @NotNull String artifactGroup() {
		if (this == ALL) return "";
		String prefix = "org.openjfx";
		String format = "%s:%s";
		
		// Resolve maven artifact with group
		return String.format(format, prefix, artifactName());
	}
	
	/**
	 * It performs the same action as the {@link #artifactGroup()} method but here the specific
	 * version of the artifact is defined.
	 *
	 * @param version The specific version of the artifact
	 * @return Name of the artifact.
	 */
	public @NotNull String artifactGroup(@NotNull String version) {
		if (this == ALL) return "";
		String format = "%s:%s";
		
		return String.format(format, artifactGroup(), version);
	}
	
	/* -----------------------------------------------------
	 * Static methods
	 * ----------------------------------------------------- */
	
	/**
	 * It performs the same action as the {@link #artifactGroup()} method but here the specific
	 * version of the artifact is defined and the artifact modifier is also defined.
	 *
	 * @param version  The specific version of the artifact
	 * @param modifier The access modifier of the artifact. Useful for separating artifacts by
	 *                 platform like JavaFX does.
	 * @return Name of the artifact.
	 */
	public @NotNull String artifactGroup(@NotNull String version, @NotNull String modifier) {
		if (this == ALL) return "";
		String format = "%s:%s";
		
		return String.format(format, artifactGroup(version), modifier);
	}
	
}
