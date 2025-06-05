import { TestBed } from '@angular/core/testing';

import { InventoriesComponent } from './inventories.component';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { InventoriesService } from '../../service/inventories.service';
import { AuthenticationService } from '../../service/authentication.service';
import { of, throwError } from 'rxjs';
import { User } from '../../model/user';
import { Inventory } from '../../model/inventory';
import { ErrorComponent } from '../error/error.component';

const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"],
};

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_INVENTORIES: Inventory[] = [
  {
    id: "1",
    name: "Inventory 1",
    description: "Description 1",
    items: [],
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: "2",
    name: "Inventory 2",
    description: "Description 2",
    items: [],
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];

describe('InventoriesComponent', () => {
  let component: InventoriesComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let inventoriesService: InventoriesService;
  let authenticationService: AuthenticationService;
  let inventoriesSpy: jasmine.Spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoriesComponent],
      providers: [provideHttpClient(), provideRouter([
        { path: 'error', component: ErrorComponent },
        { path: '**', component: InventoriesComponent },
      ])]
    })
    .compileComponents();

    injectDependencies();
    stubAuthentication();
    stubInventories();
    await initializeRouter();
    harness.fixture.autoDetectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    inventoriesService = TestBed.inject(InventoriesService);
    authenticationService = TestBed.inject(AuthenticationService);
  };

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(of(true));
  }

  function stubInventories() {
    inventoriesSpy = spyOn(inventoriesService, "getInventories").and.returnValue(of(MOCK_INVENTORIES));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/inventories", InventoriesComponent);
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the inventories names', () => {
    MOCK_INVENTORIES.forEach((inventory) => {
      const componentText = harness.routeNativeElement?.textContent;
      expect(componentText).toContain(inventory.name);
    });
  });

  it('should show the inventories descriptions', () => {
    MOCK_INVENTORIES.forEach((inventory) => {
      const componentText = harness.routeNativeElement?.textContent;
      expect(componentText).toContain(inventory.description);
    });
  });

  it('should show an error component if there is an error', async () => {
    inventoriesSpy.and.returnValue(throwError(() => new Error('Error getting inventories')));
    component.ngOnInit();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/error');
  });
});
