package com.singtel.nsb.mobile.sync.config;

import java.util.Date;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
public class CamelRouteConfig extends RouteBuilder {

	@Value("${swaggerContextPath}")
	private String contextPath;

	@Override
	public void configure() throws Exception {
		restConfiguration().component("servlet").apiContextPath("/swagger").apiContextRouteId("swagger")

				// Swagger properties
				.apiProperty("cors", "true")
				.contextPath(contextPath)
				.apiProperty("api.title", "Service Activation & Configuration")
				.apiProperty("api.version", "1.0")
				.apiProperty("api.contact.name", "Singtel")
				.apiProperty("api.contact.email", "Singtel.Admin@singtel.com")
				.apiProperty("api.contact.url", "https://Singtel.com")
				.apiProperty("host", "")
				.apiProperty("port", "8011")
				.apiProperty("schemes", "");
	}

	@GetMapping("/")
	public String ping() {
		return "Welcome to NSBHttpListener" + new Date();
	}
}