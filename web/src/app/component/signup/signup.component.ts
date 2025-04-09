import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { ApiError } from '../../model/apiError';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  signupForm = new FormGroup({
    email: new FormControl(''),
    username: new FormControl(''),
    password: new FormControl(''),
    matchingPassword: new FormControl('')
  });

  statusMessage: string | undefined;

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
      this.statusMessage = 'Passwords do not match';
      return;
    }

    this.authenticationService
      .signup(
        {
          email: this.signupForm.value.email,
          username: this.signupForm.value.username,
          password: this.signupForm.value.password,
          matchingPassword: this.signupForm.value.matchingPassword
        }
      )
      .pipe(
        catchError(
          error => {
            if (error instanceof HttpErrorResponse) {
              const errorResponse = error.error as ApiError;
              this.statusMessage = errorResponse.message;
            } else {
              this.statusMessage = 'An unknown error occurred';
            }
            return of();
          }
        )
      )
      .subscribe(() => 
        this.router.navigate(['/login'])
      );
  }
}
