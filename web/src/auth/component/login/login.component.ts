import { ChangeDetectionStrategy, Component, signal, WritableSignal } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {

  loginForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  protected readonly environment = environment;

  statusMessage: WritableSignal<string | undefined> = signal(undefined)

  constructor(
  ) {
  }

}
