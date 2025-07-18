package com.waracle.demo.cake.manager;

import org.springframework.boot.SpringApplication;

public class TestCakeManagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(CakeManagerApplication::main).with(TestContainersConfiguration.class).run(args);
	}

}
