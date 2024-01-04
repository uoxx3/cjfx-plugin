import uoxx3.cjfx.CjfxModule

plugins {
	application
	id("io.github.uoxx3.cjfx") version "1.1.0"
}

application {
	mainModule.set("uo.javafx.example")
	mainClass.set("uo.javafx.example.Example")
}

Cjfx {
	val targetModules = listOf(CjfxModule.CONTROLS, CjfxModule.FXML)
	modules.addAll(targetModules)
	
	dependencyConfigurations.clear()
	dependencyConfigurations.addAll(listOf("compileOnly"))
}