import { TestBed } from '@angular/core/testing';

import { UserBadgeComponent } from './user-badge.component';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { AuthenticationService } from '../../../service/authentication.service';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

describe('UserBadgeComponent', () => {
  const MOCK_USERNAME = "testUser";


  let component: UserBadgeComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let authenticationService: AuthenticationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserBadgeComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{
          path: '**', component: UserBadgeComponent
        }])
      ]
    })
    .compileComponents();

    injectDependencies();
    stubAuthentication();
    await initializeRouter();
    openMenu();
    harness.fixture.autoDetectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    authenticationService = TestBed.inject(AuthenticationService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "getUsername").and.returnValue(MOCK_USERNAME);
    spyOn(authenticationService, "logout").and.returnValue(of({}));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/", UserBadgeComponent);
  }

  function openMenu() {
    component.toggleMenu();
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the authenticated username', () => {
    expect(
      harness.routeNativeElement?.textContent
    ).toContain(MOCK_USERNAME);
  });

  it('should redirect to login page when the logout button is clicked', async () => {
    const logoutButton = searchForLogoutButton();
    expect(logoutButton).toBeTruthy();
    logoutButton.click();
    await harness.fixture.whenStable();
    expect(router.url).toContain('/login');
  });

  function searchForLogoutButton() {
    const buttons = Array.from(
      harness.routeNativeElement?.querySelectorAll("button") ?? []
    );
    return buttons.find((button) =>
      button.textContent?.includes("Logout")
    ) as HTMLButtonElement;
  }
});
