import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';


import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { requestedWithInterceptor } from './interceptor/requested-with.interceptor';
import { withCredentialsInterceptor } from './interceptor/with-credentials.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([requestedWithInterceptor, withCredentialsInterceptor]),
      withFetch()
    ),
    provideClientHydration(withEventReplay()),
    provideAnimationsAsync()]
};
