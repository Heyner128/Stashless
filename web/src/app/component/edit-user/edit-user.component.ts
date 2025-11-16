import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AuthenticationService} from "../../../shared/service/authentication.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {User} from "../../../shared/model/user";

@Component({
  selector: 'app-edit-user',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.scss'
})
export class EditUserComponent implements OnInit {

  editUserForm: FormGroup = new FormGroup({
    email: new FormControl(''),
    username: new FormControl(''),
    password: new FormControl(''),
    passwordConfirm: new FormControl(''),
  });

  statusMessage?: string;

  constructor(
      private readonly authenticationService: AuthenticationService,
  ) {}

  ngOnInit(): void {
    this.authenticationService
      .getCurrentUser()
      .subscribe((user: User) => {
        this.editUserForm.patchValue({
          email: user.email,
          username: user.username,
        })
      })
  }
}
