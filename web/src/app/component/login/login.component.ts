import { ChangeDetectionStrategy, Component, Inject, PLATFORM_ID, signal, WritableSignal } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule} from '@angular/forms';
import { AuthenticationService } from '../../service/authentication.service';
import { Router, RouterLink } from '@angular/router';
import { isPlatformServer } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {


  isServer = false;

  loginForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  statusMessage: WritableSignal<string | undefined> = signal(undefined)

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
    if (!this.loginForm.value.username || !this.loginForm.value.password)
      return;
    this.authenticationService
      .login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe({
        next: () => {
          this.router.navigateByUrl(this.getRedirectUrl());
        },
        error: (error: Error) => {
          this.statusMessage.set(error.message)
        }
        , 
      });
  }
}
