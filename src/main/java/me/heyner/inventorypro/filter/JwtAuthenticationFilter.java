package me.heyner.inventorypro.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import me.heyner.inventorypro.model.Cookies;
import me.heyner.inventorypro.service.JwtService;
import me.heyner.inventorypro.service.UserService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final HandlerExceptionResolver handlerExceptionResolver;

  private final JwtService jwtService;

  private final UserService userService;

  public JwtAuthenticationFilter(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
      JwtService jwtService,
      UserService userService) {
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  private String getTokenFromRequest(HttpServletRequest request) {
      boolean isOnBearerHeader = request.getHeader(HttpHeaders.AUTHORIZATION) != null
          && request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ");

      Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[0];

      boolean isOnCookie = List.of(cookies)
        .stream()
        .anyMatch(cookie -> cookie.getName().equals(Cookies.SESSION_TOKEN));

      if (isOnBearerHeader) {
          return request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
      } else if (isOnCookie) {  
          return List.of(cookies)
              .stream()
              .filter(cookie -> cookie.getName().equals(Cookies.SESSION_TOKEN))
              .findFirst()
              .orElseThrow()
              .getValue();
      } else {
          return null;
      }
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {


    final String jwtToken = getTokenFromRequest(request);

    if (jwtToken == null) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      final String jwt = jwtToken;
      final String username = jwtService.extractUsername(jwt);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (username != null && authentication == null) {
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
      }
    } catch (Exception exception) {
      handlerExceptionResolver.resolveException(request, response, null, exception);
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {

    RequestMatcher protectedRoutesMatcher = new NegatedRequestMatcher(
      new AntPathRequestMatcher("/users/**")
    );

    RequestMatcher matcher = new OrRequestMatcher(
      protectedRoutesMatcher,
      new AntPathRequestMatcher("/users/login", HttpMethod.POST.name()),
      new AntPathRequestMatcher("/users", HttpMethod.POST.name())
    );

    return matcher.matches(request);
  }



}
