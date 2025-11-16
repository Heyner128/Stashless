import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';


import { routes } from './auth.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
import {requestedWithInterceptor} from "../shared/interceptor/requested-with.interceptor";
import {authInterceptor} from "../shared/interceptor/auth.interceptor";
import {errorInterceptor} from "../shared/interceptor/error.interceptor";
import {provideOAuthClient} from "angular-oauth2-oidc";

export const authConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([requestedWithInterceptor, authInterceptor, errorInterceptor]),
      withFetch()
    ),
    provideOAuthClient(),
    provideAnimationsAsync()]
};
