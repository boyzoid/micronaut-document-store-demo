package com.boyzoid

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

@MicronautTest
class MnDocStoreDemoTest {
	
	@Inject
	EmbeddedApplication<?> application
	
	@Test
	void testItWorks() {
		assert application.running == true
	}
	
}
