package uoxx3.cjfx.extensions;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uoxx3.cjfx.CjfxModule;
import uoxx3.cjfx.internal.process.DependencyProcessor;
import uoxx3.cjfx.utilities.BaseProjectReference;
import ushiosan.jvm.collections.UList;
import ushiosan.jvm.collections.USet;
import ushiosan.jvm.platform.UArchitecture;
import ushiosan.jvm.platform.UPlatform;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Plugin configuration class. This acts as an extension and Gradle block to configure the plugin configurations in general.
 * <p>
 * It was decided to use the properties with observers, because the plugin has to track JavaFX dependencies, in this way
 * there is continuous monitoring of changes and not only in the compilation stage. This is also done this way in the
 * official JavaFX plugin, although with a completely different approach.
 */
public class CjfxConfiguration extends BaseProjectReference {
	
	/* -----------------------------------------------------
	 * Constants
	 * ----------------------------------------------------- */
	
	/**
	 * Name of the gradle extension to be used within DSL scripts
	 */
	public static final String EXTENSION_NAME = "Cjfx";
	
	/* -----------------------------------------------------
	 * Properties
	 * ----------------------------------------------------- */
	
	/**
	 * Observable property that monitors the required version of JavaFX
	 */
	public final ObjectPropertyBase<String> version;
	
	/**
	 * Observable property that monitors the target JavaFX platform
	 */
	public final ObjectPropertyBase<UPlatform> platform;
	
	/**
	 * Observable property that monitors the target JavaFX architecture
	 */
	public final ObjectPropertyBase<UArchitecture> architecture;
	
	/**
	 * Observable property that monitors JavaFX modules
	 */
	public final ObservableSet<CjfxModule> modules;
	
	/**
	 * Observable property that monitors the build configurations where
	 * JavaFX will be integrated
	 */
	public final ObservableSet<String> dependencyConfigurations;
	
	/**
	 * Helper object used to resolve project dependencies
	 */
	private final DependencyProcessor processor;
	
	/**
	 * Special property used to obtain an observable property but with a default value.
	 * <p>
	 * In particular, this property determines the version of JavaFX and if it is defined as
	 * {@code null}, it will return a default value, in this case {@code #latest#}
	 */
	private final ObservableValue<String> observableVersion;
	
	/**
	 * Special property used to obtain an observable property but with a default value.
	 * <p>
	 * In particular, this property determines the platform of JavaFX and if it is defined as
	 * {@code null}, it will return a default value, in this case {@link UPlatform#runningPlatform()}
	 */
	private final ObservableValue<UPlatform> observablePlatform;
	
	/**
	 * Special property used to obtain an observable property but with a default value.
	 * <p>
	 * In particular, this property determines the architecture of JavaFX and if it is defined as
	 * {@code null}, it will return a default value, in this case {@link UArchitecture#platformRunningArch()}
	 */
	private final ObservableValue<UArchitecture> observableArchitecture;
	
	/* -----------------------------------------------------
	 * Constructors
	 * ----------------------------------------------------- */
	
	/**
	 * Default constructor of the configuration class.
	 *
	 * @param project             The current project configuration
	 * @param dependencyProcessor Current plugin dependency processor instance
	 */
	@Inject
	public CjfxConfiguration(@NotNull Project project, @NotNull DependencyProcessor dependencyProcessor) {
		super(project);
		// Initialize properties
		processor = dependencyProcessor;
		
		version = makeProperty("version");
		observableVersion = makeObservable(version, "#latest#");
		
		platform = makeProperty("platform");
		observablePlatform = makeObservable(platform, UPlatform.runningPlatform());
		
		architecture = makeProperty("architecture");
		observableArchitecture = makeObservable(architecture, UArchitecture.platformRunningArch());
		
		dependencyConfigurations = FXCollections.observableSet("implementation", "testImplementation");
		modules = FXCollections.observableSet(CjfxModule.BASE);
		
		// Update project initial dependencies
		initializeGradleProperties();
		dependencyProcessor.updateDependencies(this);
		
		// Listen all changes
		USet.make(observableArchitecture, observableVersion, observablePlatform)
			.forEach(observable -> observable.addListener(this::onCommonPropertyChanged));
		USet.make(modules, dependencyConfigurations)
			.forEach(observable -> observable.addListener(this::onCommonSetPropertyChanged));
	}
	
	/* -----------------------------------------------------
	 * Events
	 * ----------------------------------------------------- */
	
	/**
	 * Event method used when a simple property has made a change to its content. It is not necessary to determine
	 * a specific type since the dependencies must be changed for any change made, no matter how small.
	 *
	 * @param observable The object where the change was made
	 * @param oldValue   The old value of the object
	 * @param newValue   The new value of the object
	 */
	@SuppressWarnings("unused")
	private void onCommonPropertyChanged(@NotNull ObservableValue<?> observable, Object oldValue, Object newValue) {
		processor.updateDependencies(this);
	}
	
	/**
	 * Event method used when a collection property has made a change to its content. It is not necessary to determine
	 * a specific type since the dependencies must be changed for any change made, no matter how small.
	 *
	 * @param change Event with changes made to the collection
	 */
	@SuppressWarnings("unused")
	private void onCommonSetPropertyChanged(SetChangeListener.@NotNull Change<?> change) {
		processor.updateDependencies(this);
	}
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Gets the read-only property for JavaFX version tracking
	 *
	 * @return The version observable property
	 */
	public ObservableValue<String> observableVersion() {
		return observableVersion;
	}
	
	/**
	 * Gets the read-only property for JavaFX platform tracking
	 *
	 * @return The platform observable property
	 */
	public ObservableValue<UPlatform> observablePlatform() {
		return observablePlatform;
	}
	
	/**
	 * Gets the read-only property for JavaFX architecture tracking
	 *
	 * @return The architecture observable property
	 */
	public ObservableValue<UArchitecture> observableArchitecture() {
		return observableArchitecture;
	}
	
	/* -----------------------------------------------------
	 * Internal methods
	 * ----------------------------------------------------- */
	
	/**
	 * Initializes the plugin properties using the gradle configuration files.
	 */
	private void initializeGradleProperties() {
		ExtraPropertiesExtension extras = requireProject().getExtensions()
			.findByType(ExtraPropertiesExtension.class);
		
		// Check if the extra properties exists
		if (extras == null) return;
		
		// Update the JavaFX version by configuring the project with the gradle.properties file
		getProjectExtra(extras, "version")
			.ifPresent(version::set);
		
		// Update the JavaFX platform by configuring the project with the gradle.properties file
		getProjectExtra(extras, "platform")
			.flatMap(UPlatform::from)
			.ifPresent(platform::set);
		
		// Update the JavaFX architecture by configuring the project with the gradle.properties file
		getProjectExtra(extras, "architecture")
			.map(property -> property.trim().toUpperCase())
			.map(UArchitecture::valueOf)
			.ifPresent(architecture::set);
		
		// Update the JavaFX modules by configuring the project with the gradle.properties file
		getProjectExtraAsList(extras, "modules")
			.map(list -> list.stream()
				.map(property -> property.trim().toUpperCase())
				.map(CjfxModule::valueOf)
				.collect(Collectors.toList()))
			.ifPresent(mods -> {
				modules.clear();
				modules.addAll(mods);
			});
		
		// Update the JavaFX dependencyConfigurations by configuring the project with the gradle.properties file
		getProjectExtraAsList(extras, "dependencyConfigurations")
			.ifPresent(list -> {
				dependencyConfigurations.clear();
				dependencyConfigurations.addAll(list);
			});
	}
	
	/**
	 * Create a simple observable property with a default {@code null} value
	 *
	 * @param name The property name
	 * @param <T>  The type of data that the property will have
	 * @return An observable and mutable property with a {@code null} initial value
	 */
	@Contract(pure = true)
	private <T> @NotNull ObjectPropertyBase<T> makeProperty(@NotNull String name) {
		return new SimpleObjectProperty<>(this, name, null);
	}
	
	/**
	 * From a mutable property, creates a new instance of observable value
	 * but with a default value when the original property is set to {@code null}.
	 *
	 * @param property The mutable observable property
	 * @param initial  The initial value of the property and the default value if set to {@code null}
	 * @param <T>      The type of data that the property will have
	 * @return An immutable property that is actually a proxy for the main property, but when this is set to
	 *    {@code null}, this property returns the default value (convention).
	 */
	private <T> @NotNull ObservableValue<T> makeObservable(@NotNull ObjectPropertyBase<T> property,
		@NotNull T initial) {
		property.setValue(initial);
		return property.orElse(initial);
	}
	
	/**
	 * Gets an optional object from the project extension to configure gradle.
	 * If not found then it will return an empty optional object.
	 * <p>
	 * The property will always be inside the {@code cjfx.configuration} namespace so
	 * that there is no collision with other gradle plugins.
	 *
	 * @param extension The gradle extension that handles configuration files.
	 * @param name      The name of the configuration within the file
	 * @return The value of the property or {@link Optional#empty()} if the property does not exist.
	 */
	private @NotNull Optional<String> getProjectExtra(@NotNull ExtraPropertiesExtension extension, @NotNull String name) {
		String propertyName = String.format("cjfx.configuration.%s", name);
		if (!extension.has(propertyName)) return Optional.empty();
		
		// Resolve configuration
		return Optional.ofNullable((String) extension.get(propertyName));
	}
	
	/**
	 * Gets an optional object from the project extension to configure gradle.
	 * If not found then it will return an empty optional object.
	 * Like {@link #getProjectExtra(ExtraPropertiesExtension, String)} this method returns the configuration
	 * but in list form by separating elements by {@code ;} character
	 * <p>
	 * The property will always be inside the {@code cjfx.configuration} namespace so
	 * that there is no collision with other gradle plugins.
	 *
	 * @param extension The gradle extension that handles configuration files.
	 * @param name      The name of the configuration within the file
	 * @return The value of the property or {@link Optional#empty()} if the property does not exist.
	 */
	private @NotNull Optional<List<String>> getProjectExtraAsList(@NotNull ExtraPropertiesExtension extension,
		@NotNull String name) {
		// Get extra property
		return getProjectExtra(extension, name)
			.map(property -> property.split(";"))
			.map(UList::make);
	}
	
}
