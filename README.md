# Custom JavaFX Gradle Plugin

This plugin is used to manage and configure a JavaFX project quickly and reliably. In addition to supporting cross-platform
compilation from the current platform.

## Current version

`cjfx-version = 1.1.0`

## How to use

It is currently only available for the Gradle build system. In order to use it, you must define the plugin within the project as
follows:

__Groovy DSL__

```groovy
plugins {
  id "io.github.uoxx3.cjfx" version "{cjfx-version}"
}
```

__Kotlin DSL__

```kotlin
plugins {
  id("io.github.uoxx3.cjfx") version "{cjfx-version}"
}
```

## Plugin Settings

There are several ways to configure the plugin.
The easiest way to do it is through the extension inside the __"build.gradle"__ or __"build.gradle.kts"__ file.

__Groovy DSL__

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  version.set(/* Target JavaFX version */)
  platform.set(/* Target compilation platform */)
  architecture.set(/* Target compilation architecture */)

  modules.addAll(/* All javafx modules used */)
  dependencyConfigurations.addAll(/* All configurations where modules are used */)
}
```

### Version configuration

Cjfx accepts both specific versions and dynamic versions. These dynamic versions are identified as they contain the __'#'__
characters.

__Custom version__

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  version.set("21")
}
```

__Dynamic versions__

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  version.set("#latest#") // Get the latest stable version of JavaFX
}
```

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  version.set("#early#") // Get the latest early access version of JavaFX
}
```

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  version.set("#ea\\+\\d#") // You can also use regular expressions to determine the JavaFX version.
}
```

__Platforms__

By default, the plugin determines the current platform where the project is executed, but it is possible to specify the
platform.

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  platform.set(Uplatform.LINUX)
}
```

__Architecture__

By default, the plugin determines the current architecture where the project is executed, but it is possible to specify the
architecture.

```kotlin
// Groovy & Kotlin DSL
Cjfx {
  architecture.set(UArchitecture.ARM)
}
```

__Modules__

By default, the plugin only includes the JavaFX `Base` module, and you need to specify the necessary modules.

```groovy
// Groovy DSL
import uoxx3.cjfx.CjfxModule

Cjfx {
  var targetModules = [CjfxModule.CONTROLS, CjfxModule.FXML]
  modules.addAll(targetModules)
}
```

```kotlin
// Kotlin DSL
import uoxx3.cjfx.CjfxModule

Cjfx {
  val targetModules = listOf(CjfxModule.CONTROLS, CjfxModule.FXML)
  modules.addAll(targetModules)
}
```

If all modules are required, you can use the special option `CjfxModule.ALL`
that uses all JavaFX modules.

```text
CjfxModule.ALL == CjfxModule.[BASE, GRAPHICS, CONTROLS, FXML, MEDIA, SWING, WEB]
```

__Dependency configurations__

The dependency configuration determines where and how the JavaFX application or library will be compiled. By default, the plugin
will assume that you are implementing
the modules, but you can change this behavior easily:

```groovy
// Groovy DSL
Cjfx {
  var configurations = ["compileOnly", "testCompileOnly"]

  dependencyConfiguration.clear()
  dependencyConfiguration.addAll(configurations)
}
```

```kotlin
// Kotlin DSL
Cjfx {
  val configurations = listOf("compileOnly", "testCompileOnly")

  dependencyConfiguration.clear()
  dependencyConfiguration.addAll(configurations)
}
```

### Configuration using Gradle configuration

It can also be configured through the `gradle.properties` or `cjfx.properties` files as follows:

```properties
cjfx.configuration.version=#latest#
cjfx.configuration.platform=linux
cjfx.configuration.architecture=ARM
cjfx.configuration.modules=controls; graphics; fxml
cjfx.configuration.dependencyConfiguration=compileOnly; testCompileOnly; runtimeOnly
```