import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';


import { routes } from './auth.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const authConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideAnimationsAsync()]
};
