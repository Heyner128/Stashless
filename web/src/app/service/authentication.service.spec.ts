import { TestBed } from '@angular/core/testing';

import { AuthenticationService } from './authentication.service';
import { HttpRequest, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { environment } from '../../environments/environment';

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let httpTesting: HttpTestingController;
  let cookieSpy: jasmine.Spy;
  const username = 'testing_user';
  const password = 'password';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthenticationService);
    httpTesting = TestBed.inject(HttpTestingController);
    cookieSpy = spyOnProperty(document, 'cookie', 'get').and.returnValue(
      `user_id=${username}`
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    
  });

  it('should return true if the response from the API is ok', () => {
    
    service.isAuthenticated().subscribe((response) => {
      expect(response).toBeTrue();
    });

    const req = httpTesting.expectOne(`${environment.apiBaseUrl}/users/${username}`);
    req.flush({},{ status: 200, statusText: 'OK' });
  });

  it('should return false if the response from the API is not ok', () => {
    cookieSpy.and.returnValue("");
    service.isAuthenticated().subscribe((response) => {
      expect(response).toBeFalse();
    });

    const req = httpTesting.expectOne((req: HttpRequest<any>) => {
      return req.url.startsWith(`${environment.apiBaseUrl}/users/`)
        && !req.url.endsWith(username);
    });
    req.flush({},{ status: 404, statusText: 'Not Found' });
  });

  it('login be ok if the credentials are correct', () => {
    service.login(username, password).subscribe((response) => {
      expect(response.status).toBe(200);
    });

    const req = httpTesting.expectOne((req: HttpRequest<any>) => {
      return req.url === `${environment.apiBaseUrl}/users/login`
        && req.body.username === username
        && req.body.password === password;
    });
    req.flush({},{ status: 200, statusText: 'OK' });
  });

  it('login should fail if the credentials are incorrect', () => {
    service.login('randomusername', 'randompassword').subscribe((response) => {
      expect(response.ok).toBeFalse();
    });

    const req = httpTesting.expectOne((req: HttpRequest<any>) => {
      return req.url === `${environment.apiBaseUrl}/users/login`
        && !(req.body.username === username
        && req.body.password === password);
    });
    req.flush({},{ status: 401, statusText: 'Unauthorized' });
  });

});
