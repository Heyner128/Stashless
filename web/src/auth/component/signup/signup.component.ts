import { ChangeDetectionStrategy, Component, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../shared/service/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'auth-signup',
  imports: [ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SignupComponent {
  signupForm = new FormGroup({
    email: new FormControl(''),
    username: new FormControl(''),
    password: new FormControl(''),
    matchingPassword: new FormControl('')
  });

  statusMessage: WritableSignal<string | undefined> = signal(undefined)

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router
  ) {}

  signup() {
    if (
      !this.signupForm.value.email ||
      !this.signupForm.value.username ||
      !this.signupForm.value.password ||
      !this.signupForm.value.matchingPassword
    ) {
      return;
    }
    if (this.signupForm.value.password !== this.signupForm.value.matchingPassword) {
      this.statusMessage.set('Passwords do not match');
      return;
    }

    this.authenticationService
      .signup({
        email: this.signupForm.value.email,
        username: this.signupForm.value.username,
        password: this.signupForm.value.password,
        matchingPassword: this.signupForm.value.matchingPassword,
      })
      .subscribe({
        next: () => {this.router.navigate(["/login"])},
        error: (error: Error) => this.statusMessage.set(error.message),
      });
  }
}
