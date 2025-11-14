import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';

import { requestedWithInterceptor } from './requested-with.interceptor';
import { Observable, of } from 'rxjs';
import { environment } from '../environments/environment';

describe('requestedWithInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) => 
    TestBed.runInInjectionContext(() => requestedWithInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should set X-Requested-With to XMLHttpRequest', () => {
    const mockHandler = (_: HttpRequest<unknown>): Observable<HttpResponse<unknown>> => of(new HttpResponse<unknown>());

    const mockRequest = new HttpRequest<any>("GET", `${environment.apiBaseUrl}`);

    const spy = jasmine.createSpy().and.callFake(mockHandler);

    interceptor(mockRequest, spy).subscribe(() => {
      expect(spy).toHaveBeenCalledWith(
        mockRequest.clone({ setHeaders: { 'X-Requested-With': 'XMLHttpRequest' } })
      );
    }); 
  });
});
