import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { UserBadgeComponent } from "./user-badge/user-badge.component";

@Component({
  selector: "app-layout",
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    UserBadgeComponent,
  ],
  templateUrl: "./layout.component.html",
  styleUrl: "./layout.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LayoutComponent implements AfterViewInit {

  @ViewChild("sideNav") sideNav: ElementRef<HTMLElement> | undefined;
  opened: boolean = false;

  ngAfterViewInit(): void {
    this.closeSideNav();
  }

  toggleSideNav() {
    if (this.opened) {
      this.closeSideNav();
    } else {
      this.openSideNav();
    }
  }

  private closeSideNav() {
    if(!this.sideNav) return;
    this.sideNav.nativeElement.classList.add("nav__mobile-closed");
    this.sideNav.nativeElement.classList.remove("nav__mobile-open");
    this.opened = false;
  }
  private openSideNav() {
    if(!this.sideNav) return;
    this.sideNav.nativeElement.classList.remove("nav__mobile-closed");
    this.sideNav.nativeElement.classList.add("nav__mobile-open");
    this.opened = true;
  }
}
