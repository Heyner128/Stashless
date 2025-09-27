package me.heyner.stashless.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import me.heyner.stashless.model.Cookies;
import me.heyner.stashless.service.JwtService;
import me.heyner.stashless.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
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

  private @Nullable String getTokenFromRequest(HttpServletRequest request) {
    boolean isOnBearerHeader =
        request.getHeader(HttpHeaders.AUTHORIZATION) != null
            && request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ");

    Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[0];

    boolean isOnCookie =
        Stream.of(cookies).anyMatch(cookie -> cookie.getName().equals(Cookies.SESSION_TOKEN));

    if (isOnBearerHeader) {
      return request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
    } else if (isOnCookie) {
      return Stream.of(cookies)
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
      final String username = jwtService.extractUsername(jwtToken);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (username != null && authentication == null) {
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwtToken, userDetails)) {
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
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

    RequestMatcher protectedRoutesMatcher =
        new NegatedRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/users/**"));

    RequestMatcher matcher =
        new OrRequestMatcher(
            protectedRoutesMatcher,
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/users/login"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/users"));

    return matcher.matches(request);
  }
}
