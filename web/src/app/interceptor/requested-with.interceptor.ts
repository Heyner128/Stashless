import { HttpInterceptorFn } from '@angular/common/http';

export const requestedWithInterceptor: HttpInterceptorFn = (req, next) => {
  req = req.clone({
    setHeaders: {
      'X-Requested-With': 'XMLHttpRequest',
    },
  });
  return next(req);
};
