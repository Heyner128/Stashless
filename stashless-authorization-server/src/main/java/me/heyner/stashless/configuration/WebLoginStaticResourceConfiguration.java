package me.heyner.stashless.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebLoginStaticResourceConfiguration implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(
            "/{filename:.*\\.css|.*\\.ico|.*\\.svg|.*\\.webp|.*\\.js|.*\\.js.map|.*\\.css.map|.*\\.woff|.*\\.woff2|.*\\.png}")
        .addResourceLocations("classpath:/");
  }
}
