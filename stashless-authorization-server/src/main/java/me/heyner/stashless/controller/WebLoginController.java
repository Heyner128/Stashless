package me.heyner.stashless.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/login", "/signup"})
public class WebLoginController {
  @GetMapping
  public void login(HttpServletResponse response) throws IOException {
    ClassPathResource htmlFile = new ClassPathResource("login.html");
    response.setContentType(MediaType.TEXT_HTML_VALUE);
    try (var in = htmlFile.getInputStream();
        var out = response.getOutputStream()) {
      in.transferTo(out);
    }
    response.flushBuffer();
  }
}
