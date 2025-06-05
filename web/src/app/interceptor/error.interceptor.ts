import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs';
import { ApiError } from '../model/apiError';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if(isNetworkError(error)) {
        throw new Error(error.message);
      }
      if(isBackendError(error)) {
        const errorResponseBody = error.error as ApiError;
        throw new Error(errorResponseBody.message);
      };
      throw new Error('Unknown error');
    })
  );
};

function isNetworkError(error: HttpErrorResponse):boolean {
  return error.status === 0;
}

function isBackendError(error: HttpErrorResponse):boolean {
  return error.status > 0;
}


