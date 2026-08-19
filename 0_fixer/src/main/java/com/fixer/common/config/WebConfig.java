package com.fixer.common.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String absoluteDir = new File(uploadDir).getAbsolutePath();

		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:" + absoluteDir + File.separator);
	}
}