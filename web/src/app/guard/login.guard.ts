import { CanActivateFn } from '@angular/router';
import {inject} from '@angular/core';
import { AuthenticationService } from '../../shared/service/authentication.service';




export const loginGuard: CanActivateFn = (_, state) => {
  const authenticationService = inject(AuthenticationService);
  if(authenticationService.isAuthenticated()) {
    return true;
  }
  return authenticationService.login()
};
