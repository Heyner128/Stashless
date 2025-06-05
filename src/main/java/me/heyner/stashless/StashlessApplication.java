package me.heyner.stashless;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Inventory Pro",
            version = "0.1",
            description = "Inventory Pro API documentation",
            contact =
                @Contact(
                    url = "https://heyner.me",
                    name = "Heyner Cuevas",
                    email = "admin@heyner.me")))
@SpringBootApplication
public class StashlessApplication {

  public static void main(String[] args) {
    SpringApplication.run(StashlessApplication.class, args);
  }
}
