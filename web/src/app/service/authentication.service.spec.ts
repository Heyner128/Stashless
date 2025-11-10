import { TestBed } from '@angular/core/testing';

import { AuthenticationService } from './authentication.service';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { errorInterceptor } from '../interceptor/error.interceptor';
import { ApiTesting } from '../../testing/api';

describe('AuthenticationService', () => {
  

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(
          withInterceptors([
            errorInterceptor,
          ]),
          withFetch()
        ),
        provideHttpClientTesting(),
      ],
    });
  });

});
