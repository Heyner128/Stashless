import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthenticationService } from '../service/authentication.service';
import { tap } from 'rxjs';




export const loginGuard: CanActivateFn = (_, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  return authenticationService.isAuthenticated().pipe(
    tap(
      isAuthenticated => {
        if (!isAuthenticated) {
          router.navigate(['/login'], {
            queryParams: { redirectUrl: state.url },
          });
        }
      }
    )
  );
};
