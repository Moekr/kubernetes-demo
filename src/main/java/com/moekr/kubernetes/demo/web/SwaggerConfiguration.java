package com.moekr.kubernetes.demo.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
	@Bean
	public Docket newsApi() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		docket.enable(true);
		docket.apiInfo(apiInfo()).select().paths(PathSelectors.ant("/api/**")).build();
		return docket;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("K8S Demo")
				.description("K8S Demo Api Reference")
				.version("1.0")
				.build();
	}
}
