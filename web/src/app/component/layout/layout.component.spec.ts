import { TestBed } from '@angular/core/testing';

import { LayoutComponent } from './layout.component';
import { provideRouter, Router } from '@angular/router';
import { routes } from '../../app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { AuthenticationService } from '../../service/authentication.service';
import { of } from 'rxjs';
import { InventoriesService } from '../../service/inventories.service';

describe('LayoutComponent', () => {
  let component: LayoutComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let authenticationService: AuthenticationService;
  let inventoriesService: InventoriesService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{
          path: '**', component: LayoutComponent
        }]),
        provideAnimationsAsync(),
      ],
    }).compileComponents();

    injectDependencies();
    stubAuthentication();
    stubInventories();
    await initializeRouter();

    harness.fixture.autoDetectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    authenticationService = TestBed.inject(AuthenticationService);
    inventoriesService = TestBed.inject(InventoriesService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(true);
  }

  function stubInventories() {
    spyOn(inventoriesService, "getInventories").and.returnValue(of([]));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/", LayoutComponent);
  }

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it('should go to / when clicking on the logo', async () => {
    const logo = harness.routeNativeElement!.querySelector('a.header__logo') as HTMLAnchorElement;
    expect(logo).toBeTruthy();
    logo.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/');
  });

  it('should go to / when clicking on the home link', async () => {
    const homeLink = searchForNavigationLink('Home');
    expect(homeLink).toBeTruthy();
    homeLink!.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/');
  });

  function searchForNavigationLink(linkText: string): HTMLAnchorElement | undefined {
    const links: HTMLAnchorElement[] = Array.from(harness.routeNativeElement!.querySelectorAll('.nav a'));
    return links.find(link => link.textContent?.includes(linkText));
  }

  it('should go to /inventories when clicking on the inventories link', async () => {
    const inventoriesLink = searchForNavigationLink('Inventories');
    expect(inventoriesLink).toBeTruthy();
    inventoriesLink!.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/inventories');
  });
});
