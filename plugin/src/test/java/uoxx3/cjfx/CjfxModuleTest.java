package uoxx3.cjfx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CjfxModuleTest {
	
	@Test
	public void artifactNameTest() {
		CjfxModule module = CjfxModule.FXML;
		String expected = "javafx-fxml";
		
		// Assert content
		Assertions.assertEquals(expected, module.artifactName(),
								"Invalid artifact name");
		
		// Display information
		System.out.printf("[Expected - Actual]: %s - %s%n", expected,
						  module.artifactName());
	}
	
	@Test
	public void artifactGroupTest() {
		CjfxModule module = CjfxModule.FXML;
		String expected = "org.openjfx:javafx-fxml";
		
		// Assert content
		Assertions.assertEquals(expected, module.artifactGroup(),
								"Invalid artifact group name");
		
		// Display information
		System.out.printf("[Expected - Actual]: %s - %s%n", expected,
						  module.artifactGroup());
	}
	
}