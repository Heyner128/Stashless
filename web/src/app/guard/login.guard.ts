import { CanActivateFn, Router } from '@angular/router';
import {afterNextRender, inject, PLATFORM_ID} from '@angular/core';
import { AuthenticationService } from '../service/authentication.service';
import { tap } from 'rxjs';
import {isPlatformBrowser} from "@angular/common";




export const loginGuard: CanActivateFn = (_, state) => {
  const authenticationService = inject(AuthenticationService);
  if(authenticationService.isAuthenticated()) {
    return true;
  }
  return authenticationService.login()
};
