import { TestBed } from '@angular/core/testing';

import { AuthenticationService } from './authentication.service';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { errorInterceptor } from '../interceptor/error.interceptor';
import { ApiTesting } from '../../testing/api';

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let apiTesting: ApiTesting;
  let cookieSpy: jasmine.Spy;
  const MOCK_USERNAME = 'testing_user';
  const MOCK_PASSWORD = 'password';
  

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(
          withInterceptors([
            errorInterceptor,
          ]),
          withFetch()
        ),
        provideHttpClientTesting(),
      ],
    });
    injectDependencies();
    initCookieSpy();
    mockUserIdCookie();
  });

  function injectDependencies() {
    service = TestBed.inject(AuthenticationService);
    apiTesting = TestBed.inject(ApiTesting);
  }

  function initCookieSpy() {
    cookieSpy = spyOnProperty(document, "cookie", "get");
  }

  function mockUserIdCookie(value: string = MOCK_USERNAME) {
    cookieSpy.and.returnValue(`userId=${value};`);
  }

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return true if the response from the API is ok', () => {
    
    service.isAuthenticated().subscribe((response) => {
      expect(response).toBeTrue();
    });
    apiTesting.expectSuccessfulApiResponse();
  });

  it('should return false if the response from the API is not ok', () => {
    mockUserIdCookie("");
    service.isAuthenticated().subscribe((response) => {
      expect(response).toBeFalse();
    });
    apiTesting.expectUnsuccessfulApiResponse({
      status: 401,
      message: 'Unauthorized'
    });
  });

  it('login be ok if the credentials are correct', () => {
    service.login(MOCK_USERNAME, MOCK_PASSWORD).subscribe((response) => {
      expect(response).toBeDefined();
    });
    apiTesting.expectSuccessfulApiResponse();
  });

  it('login should fail if the credentials are incorrect', () => {

    service.login('randomusername', 'randompassword')
    .subscribe({
      next: () => fail('should have failed with a 401 error'),
      error: (error: Error) => {
        expect(error).toBeDefined();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse({
      status: 401,
      message: 'Unauthorized'
    });
  });

  it('logout should be ok if the api response is ok', () => {
    service.logout().subscribe((response) => {
      expect(response).toBeDefined();
    });
    apiTesting.expectSuccessfulApiResponse();
  });

  it('logout should fail if the api response is not ok', () => {
    service.logout().subscribe({
      next: () => fail('should have failed with a 401 error'),
      error: (error: Error) => {
        expect(error).toBeDefined();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

});
