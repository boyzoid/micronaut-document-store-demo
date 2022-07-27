package com.boyzoid

import groovy.transform.CompileStatic
import io.micronaut.context.env.Environment
import io.micronaut.runtime.Micronaut

@CompileStatic
class Application {
	static void main(String[] args) {
		Micronaut.build(args)
				.mainClass(Application.class)
				.defaultEnvironments(Environment.DEVELOPMENT)
				.start();
	}
}
