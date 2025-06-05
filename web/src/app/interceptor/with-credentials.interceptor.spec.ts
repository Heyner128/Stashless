import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';

import { withCredentialsInterceptor } from './with-credentials.interceptor';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

describe('withCredentialsInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) => 
    TestBed.runInInjectionContext(() => withCredentialsInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should set withCredentials to true', () => {
    const mockHandler = (_: HttpRequest<unknown>): Observable<HttpResponse<unknown>> => of(new HttpResponse<unknown>());

    const spy = jasmine.createSpy().and.callFake(mockHandler);

    interceptor(new HttpRequest<any>("GET", `${environment.apiBaseUrl}`), spy).subscribe(() => {
      expect(spy).toHaveBeenCalledWith(
        jasmine.objectContaining({ withCredentials: true })
      );
    }); 
  });
});
