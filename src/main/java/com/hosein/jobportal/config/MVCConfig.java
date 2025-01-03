package com.hosein.jobportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "photos";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposedDirectory(registry);
    }

    private void exposedDirectory(ResourceHandlerRegistry registry) {
        Path path = Paths.get(UPLOAD_DIR);
        registry.addResourceHandler("/" + UPLOAD_DIR + "/**")
                .addResourceLocations("file:" + path.toAbsolutePath() + "/");
    }
}
