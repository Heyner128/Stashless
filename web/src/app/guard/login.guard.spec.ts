import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, Router} from '@angular/router';

import { loginGuard } from './login.guard';
import { AuthenticationService } from '../service/authentication.service';
import { Observable, of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';

describe('loginGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => loginGuard(...guardParameters));
  let mockAuthenticationService: AuthenticationService
  let mockRouter = {
    navigate: jasmine.createSpy('navigate'),
    navigateByUrl: jasmine.createSpy('navigateByUrl'),
  }
  let spyIsAuthenticated: jasmine.Spy;

  beforeEach(() => {
    
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: Router,
          useValue: mockRouter,
        }
      ],
    });
    mockAuthenticationService = TestBed.inject(AuthenticationService);
    spyIsAuthenticated = spyOn(
      mockAuthenticationService,
      'isAuthenticated'
    ).and.returnValue(of(true));
    
  });

  afterEach(() => {
    Object.values(mockRouter).forEach((spy) => spy.calls.reset());
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('should redirect if the user is not authenticated', () => {
    spyIsAuthenticated.and.returnValue(of(false));
    const guardResult = executeGuard(
      new ActivatedRouteSnapshot(),
      { root: new ActivatedRouteSnapshot, url: '/test' }
    ) as Observable<boolean>

    guardResult.subscribe(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login'], {
        queryParams: { redirectUrl: '/test' },
      });
    });
  });

  it('should not redirect if the user is authenticated', () => {
    const guardResult = executeGuard(
      new ActivatedRouteSnapshot(),
      { root: new ActivatedRouteSnapshot, url: '/test' }
    ) as Observable<boolean>

    guardResult.subscribe(() => {
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });
  });
});
