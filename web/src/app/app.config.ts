import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';


import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { requestedWithInterceptor } from './interceptor/requested-with.interceptor';
import { authInterceptor } from './interceptor/auth.interceptor';
import { errorInterceptor } from './interceptor/error.interceptor';
import {provideOAuthClient} from "angular-oauth2-oidc";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([requestedWithInterceptor, authInterceptor, errorInterceptor]),
      withFetch()
    ),
    provideClientHydration(withEventReplay()),
    provideOAuthClient(),
    provideAnimationsAsync()]
};
