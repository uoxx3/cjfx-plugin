@file:Suppress("UnstableApiUsage")

/*
* This file was generated by the Gradle 'init' task.
*
* This generated file contains a sample Gradle plugin project to get you started.
* For more details on writing Custom Plugins, please refer to https://docs.gradle.org/8.5/userguide/custom_plugins.html in the Gradle documentation.
*/
plugins {
	// Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
	`java-gradle-plugin`
	id("org.javamodularity.moduleplugin") version "1.8.12"
	id("com.gradle.plugin-publish") version "1.2.1"
}

val pluginName: String get() = "cjfx"
val pluginId: String get() = "uoxx3.$pluginName"

group = "io.github.uoxx3"
version = "1.0.0"

/* -----------------------------------------------------
 * Java configuration
 * ----------------------------------------------------- */

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

/* -----------------------------------------------------
 * Plugin configuration
 * ----------------------------------------------------- */

gradlePlugin {
	website.set("https://github.com/uoxx3/cjfx-plugin")
	vcsUrl.set("https://github.com/uoxx3/cjfx-plugin.git")
	
	// Plugin definition
	val cjfx by plugins.creating {
		description = "Create projects with JavaFX with different configurations from the same platform"
		displayName = "Custom JavaFx Plugin"
		id = pluginId
		implementationClass = "uoxx3.cjfx.CjfxPluginEntry"
		version = project.version as String
		tags.set(listOf("java", "javafx", "manager"))
	}
}

afterEvaluate {
	publishing {
		publications {
			withType(MavenPublication::class.java) {
				val artifactName = "plugin"
				if (artifactId.endsWith(artifactName)) {
					val part = artifactId.substring(0, (artifactId.length - artifactName.length))
					artifactId = if (part.isBlank()) pluginName else "$part.$pluginName"
				}
			}
		}
	}
}

/* -----------------------------------------------------
 * Testing configuration
 * ----------------------------------------------------- */

val functionalTestSourceSet = sourceSets.create("functionalTest") {}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

/* -----------------------------------------------------
 * Task configurations
 * ----------------------------------------------------- */

val functionalTest by tasks.registering(Test::class) {
	testClassesDirs = functionalTestSourceSet.output.classesDirs
	classpath = functionalTestSourceSet.runtimeClasspath
	useJUnitPlatform()
}

tasks.named<Task>("check") {
	dependsOn(functionalTest)
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

/* -----------------------------------------------------
 * Plugin dependencies
 * ----------------------------------------------------- */

dependencies {
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.github.ushiosan23:jvm-utilities:1.0.0")
	compileOnly("org.jetbrains:annotations:24.1.0")
	// Use JUnit Jupiter for testing.
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}