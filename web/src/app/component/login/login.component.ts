import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule} from '@angular/forms';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { isPlatformServer } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {


  isServer = false;

  loginForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  statusMessage: string | undefined;

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router,
    @Inject(PLATFORM_ID) private readonly platformId: Object
  ) {
    this.isServer = isPlatformServer(this.platformId);
  }

  private getRedirectUrl(): string {
    const url = this.router.parseUrl(this.router.url);
    return url.queryParams['redirectUrl'] || '/';
  }

  login() {
    if(!this.loginForm.value.username || !this.loginForm.value.password) return;
    this.authenticationService
      .login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe(
        (response: HttpResponse<Object>) => {
          if(response.ok) {
            this.router.navigate([this.getRedirectUrl()]);
          } else {
            this.statusMessage = 'Invalid credentials';
          }
        }
      )
  }
}
