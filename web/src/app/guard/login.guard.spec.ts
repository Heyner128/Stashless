import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, Router} from '@angular/router';

import { loginGuard } from './login.guard';
import { AuthenticationService } from '../service/authentication.service';
import { Observable, of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';

describe('loginGuard', () => {
  beforeEach(() => {
    
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
      ],
    });
    
  });
});
