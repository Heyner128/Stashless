import {  TestBed } from '@angular/core/testing';

import { SignupComponent } from './signup.component';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthenticationService } from '../../service/authentication.service';
import { of, throwError } from 'rxjs';
import { User } from '../../model/user';
import { InputTesting } from '../../../testing/input';

describe('SignupComponent', () => {
  const MOCK_USER: User = {
    username: 'testuser',
    email: 'testuser@test.com',
    authorities: ['USER'],
  }
  const MOCK_PASSWORD = 'testpassword';

  let nativeElement: HTMLElement | null;
  let signupButton: HTMLButtonElement;
  let emailInput: HTMLInputElement;
  let usernameInput: HTMLInputElement;
  let passwordInput: HTMLInputElement;
  let matchingPasswordInput: HTMLInputElement;

  let component: SignupComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let authenticationService: AuthenticationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignupComponent],
      providers: [
        provideRouter([{
          path: '**', component: SignupComponent
        }]),
        provideHttpClient()
      ]
    })
    .compileComponents();

    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/signup", SignupComponent);
    nativeElement = harness.routeNativeElement;
    router = TestBed.inject(Router);
    authenticationService = TestBed.inject(AuthenticationService);

    signupButton = nativeElement!.querySelector('button[type="submit"]') as HTMLButtonElement;
    emailInput = nativeElement!.querySelector('#email') as HTMLInputElement;
    usernameInput = nativeElement!.querySelector('#username') as HTMLInputElement;
    passwordInput = nativeElement!.querySelector('#password') as HTMLInputElement;
    matchingPasswordInput = nativeElement!.querySelector('#matchingPassword') as HTMLInputElement;
    harness.fixture.autoDetectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to login if the signup is successful', async () => {
    spyOn(authenticationService, "signup").and.returnValue(
     of(MOCK_USER)
    );

    InputTesting.insertText(emailInput, MOCK_USER.email);
    InputTesting.insertText(usernameInput, MOCK_USER.username);
    InputTesting.insertText(passwordInput, MOCK_PASSWORD);
    InputTesting.insertText(matchingPasswordInput, MOCK_PASSWORD);

    signupButton.click();
    await harness.fixture.whenStable();


    expect(router.url).toBe("/login");
  });

  it('should show an error message if the passwords do not match', async () => {
    spyOn(authenticationService, "signup").and.returnValue(
      of(
        MOCK_USER
      )
    );

    InputTesting.insertText(emailInput, MOCK_USER.email);
    InputTesting.insertText(usernameInput, MOCK_USER.username);
    InputTesting.insertText(passwordInput, MOCK_PASSWORD);
    InputTesting.insertText(matchingPasswordInput, `${MOCK_PASSWORD}randomstring`);

    signupButton.click();
    await harness.fixture.whenStable();

    const statusMessage = nativeElement!.querySelector(
      ".signup-form__status"
    ) as HTMLDivElement;
    expect(statusMessage?.textContent).toBe("Passwords do not match");
  });

  it('should show an error message if the signup fails', async () => {
    spyOn(authenticationService, "signup").and.returnValue(
      throwError(() => "Signup failed")
    );

    InputTesting.insertText(emailInput, MOCK_USER.email);
    InputTesting.insertText(usernameInput, MOCK_USER.username);
    InputTesting.insertText(passwordInput, MOCK_PASSWORD);
    InputTesting.insertText(matchingPasswordInput, MOCK_PASSWORD);

    signupButton.click();
    await harness.fixture.whenStable();

    const statusMessage = nativeElement!.querySelector(
      ".signup-form__status"
    ) as HTMLDivElement;
    expect(statusMessage?.textContent).toBe("Signup failed");
  });
});
