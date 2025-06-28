import { TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { AuthenticationService } from '../../service/authentication.service';
import { of, throwError } from 'rxjs';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { User } from '../../model/user';
import { Input } from '@angular/core';
import { InputTesting } from '../../../testing/input';

describe('LoginComponent', () => {
  const MOCK_USER: User = {
    username: 'testuser',
    email: 'testuser@test.com',
    authorities: ['USER'],
  }
  const MOCK_PASSWORD = 'testpassword';

  let nativeElement: HTMLElement | null;
  let loginButton: HTMLButtonElement;
  let usernameInput: HTMLInputElement;
  let passwordInput: HTMLInputElement;
  let signUpLink: HTMLAnchorElement;



  let component: LoginComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let authenticationService: AuthenticationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([
          { path: '**', component: LoginComponent },
        ]),
        provideHttpClient(),
      ],
    }).compileComponents();

    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/login", LoginComponent);
    router = TestBed.inject(Router);
    authenticationService = TestBed.inject(AuthenticationService);

    nativeElement = harness.routeNativeElement;
    loginButton = nativeElement!.querySelector('button[type="submit"]') as HTMLButtonElement;
    usernameInput = nativeElement!.querySelector('#username') as HTMLInputElement;
    passwordInput = nativeElement!.querySelector('#password') as HTMLInputElement;
    signUpLink = nativeElement!.querySelector('a') as HTMLAnchorElement;
    harness.fixture.autoDetectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("it should go to sign up when the sign up link is clicked", async () => {
    signUpLink!.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe("/signup");
  });
  
  it('should login when the login button is clicked', async () => {
    spyOn(authenticationService, "login").and.returnValue(
      of(
        MOCK_USER
      )
    );

    InputTesting.insertText(usernameInput, MOCK_USER.username);
    InputTesting.insertText(passwordInput, MOCK_PASSWORD);
    loginButton.click();

    await harness.fixture.whenStable();

    expect(router.url).toBe('/');
  });

  it('should show an error message when login fails', async () => {
    spyOn(authenticationService, "login").and.returnValue(
      throwError(() => new Error("Login failed"))
    );

    InputTesting.insertText(usernameInput, MOCK_USER.username);
    InputTesting.insertText(passwordInput, MOCK_PASSWORD);
    loginButton.click();

    await harness.fixture.whenStable();
    
    const statusMessage = nativeElement!.querySelector(
      ".login-form__status"
    ) as HTMLDivElement;
    
    expect(statusMessage?.textContent).toBe('Login failed');
  });
});
