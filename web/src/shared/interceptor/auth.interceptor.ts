import { HttpInterceptorFn } from '@angular/common/http';
import {AuthenticationService} from "../service/authentication.service";
import {inject} from "@angular/core";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authenticationService = inject(AuthenticationService);
  const authToken = authenticationService.getAccessToken()
  if(authenticationService.isAuthenticated()) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });
  }
  return next(req);
};
