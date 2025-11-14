import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpEvent, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';

import { errorInterceptor } from './error.interceptor';
import { environment } from '../environments/environment';
import { Observable, of, throwError } from 'rxjs';

describe('errorInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) => 
    TestBed.runInInjectionContext(() => errorInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should catch api http errors and extract messages', () => {
    const mockHandler = (
      _: HttpRequest<unknown>
    ): Observable<HttpEvent<unknown>> =>
      throwError(
        () =>
          new HttpErrorResponse({
            status: 500,
            statusText: "Internal Server Error",
            error: { message: "Internal Server Error" },
          })
      );

    const mockRequest = new HttpRequest<any>("GET", `${environment.apiBaseUrl}`);

    const spy = jasmine.createSpy().and.callFake(mockHandler);

    interceptor(mockRequest, spy).subscribe({
      next: () => {
        fail("should not be called");
      },
      error: (error: Error) => {
        expect(error.message).toBe("Internal Server Error");
      },
    })
  })

  it("should catch network http errors and extract messages", () => {

    const mockHandler = (
      _: HttpRequest<unknown>
    ): Observable<HttpEvent<unknown>> =>
      throwError(
        () =>
          new HttpErrorResponse({
            status: 0,
            statusText: "Network Error",
          })
      );

    const mockRequest = new HttpRequest<any>(
      "GET",
      `${environment.apiBaseUrl}`
    );

    const spy = jasmine.createSpy().and.callFake(mockHandler);

    interceptor(mockRequest, spy).subscribe({
      next: () => {
        fail("should not be called");
      },
      error: (error: Error) => {
        expect(error).toBeDefined();
      },
    });
  });
});
