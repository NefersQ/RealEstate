package demo.RealEstate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${upload.models.path}")
  private String modelPath;

  @Value("${upload.images.path}")
  private String imagePath;
  // resource handler that configure paths to models and images
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/models/**")
            .addResourceLocations("file:" + modelPath);

    registry.addResourceHandler("/images/**")
            .addResourceLocations("file:" + imagePath);
  }
}
