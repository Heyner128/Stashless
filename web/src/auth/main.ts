import { bootstrapApplication } from '@angular/platform-browser';
import { authConfig } from './auth.config';
import { AuthComponent } from './component/auth.component';

bootstrapApplication(AuthComponent, authConfig)
  .catch((err) => console.error(err));
