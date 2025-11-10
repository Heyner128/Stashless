import { ChangeDetectionStrategy, Component, signal, WritableSignal} from '@angular/core';
import { AuthenticationService } from '../../../service/authentication.service';
import {Router, RouterLink} from '@angular/router';
import { PopoverComponent } from "../../popover/popover.component";
import {AsyncPipe} from "@angular/common";
import {map, Observable} from "rxjs";

@Component({
  selector: 'app-user-badge',
  imports: [PopoverComponent, RouterLink, AsyncPipe],
  templateUrl: './user-badge.component.html',
  styleUrl: './user-badge.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserBadgeComponent {
  isMenuOpen: WritableSignal<boolean> = signal(false);
  username$: Observable<string>

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router
  ) {
    this.username$ = this.authenticationService.getUsername()
  }


  toggleMenu() {
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  logout() {
    this.authenticationService.logout();
  }
}
