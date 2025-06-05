import { TestBed } from '@angular/core/testing';

import { EditInventoryComponent } from './edit-inventory.component';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { InventoriesService } from '../../../service/inventories.service';
import { AuthenticationService } from '../../../service/authentication.service';
import { Inventory } from '../../../model/inventory';
import { User } from '../../../model/user';
import { of, throwError } from 'rxjs';
import { InputTesting } from '../../../../testing/input';
import { InventoriesComponent } from '../inventories.component';
import { ErrorComponent } from '../../error/error.component';

const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"],
};

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_INVENTORY: Inventory = {
  id: "1",
  name: "Inventory 1",
  description: "Description 1",
  items: [],
  user: MOCK_USER,
  createdAt: MOCK_DATE,
  updatedAt: MOCK_DATE,
};

describe('EditInventoryComponent', () => {
  let component: EditInventoryComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let inventoriesService: InventoriesService;
  let authenticationService: AuthenticationService;
  let getInventorySpy: jasmine.Spy;
  let updateInventorySpy: jasmine.Spy;
  let deleteInventorySpy: jasmine.Spy;

  let nameInput: HTMLInputElement | null;
  let descriptionInput: HTMLTextAreaElement | null;
  let submitButton: HTMLButtonElement | null;
  let deleteButton: HTMLButtonElement | null;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditInventoryComponent],
      providers: [provideRouter([
        { path: "inventories/:id", component: EditInventoryComponent },
        { path: "inventories", component: InventoriesComponent },
        { path: "error", component: ErrorComponent },
      ]), provideHttpClient()]
    })
    .compileComponents();
    injectDependencies();
    stubAuthentication();
    stubInventories();

    await initializeRouter();
    searchForInputs();
    harness.detectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    inventoriesService = TestBed.inject(InventoriesService);
    authenticationService = TestBed.inject(AuthenticationService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(of(true));
  }

  function stubInventories() {
    updateInventorySpy = spyOn(inventoriesService, "updateInventory").and.returnValue(of(MOCK_INVENTORY));
    getInventorySpy = spyOn(inventoriesService, "getInventory").and.returnValue(of(MOCK_INVENTORY));
    deleteInventorySpy = spyOn(inventoriesService, "deleteInventory").and.returnValue(of({}));
  }
  
  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl(`/inventories/${MOCK_INVENTORY.id}`, EditInventoryComponent);
  }

  function searchForInputs() {
    nameInput = harness.routeNativeElement!.querySelector("input[name='name']");
    descriptionInput = harness.routeNativeElement!.querySelector(
      "textarea[name='description']"
    );
    deleteButton = harness.routeNativeElement!.querySelector(
      "button[type='button']"
    );
    submitButton = harness.routeNativeElement!.querySelector(
      "button[type='submit']"
    );
  }

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should initialize the form with inventory data", async () => {
    expect(nameInput?.value).toBe(MOCK_INVENTORY.name);
    expect(descriptionInput?.value).toBe(MOCK_INVENTORY.description);
  });

  it("should redirect to /inventories if the update is successful", async () => {
    InputTesting.insertText(nameInput!, "Updated Inventory");
    InputTesting.insertText(descriptionInput!, "Updated Description");
    submitButton?.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe("/inventories");
  });

  it("should show an error message if the update fails", async () => {
    const errorMessage = "Error updating inventory";
    updateInventorySpy.and.returnValue(throwError(() => new Error(errorMessage)));
    InputTesting.insertText(nameInput!, "Updated Inventory");
    InputTesting.insertText(descriptionInput!, "Updated Description");
    submitButton?.click();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(harness.routeNativeElement?.textContent).toContain(errorMessage);
  });

  it("should delete the inventory and redirect to /inventories", async () => {
    deleteButton?.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe("/inventories");
  });

  it("should show an error message if the delete fails", async () => {
    const errorMessage = "Error deleting inventory";
    deleteInventorySpy.and.returnValue(throwError(() => new Error(errorMessage)));
    deleteButton?.click();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(harness.routeNativeElement?.textContent).toContain(errorMessage);
  });

  it("should redirect to error page if the inventory is not found", async () => {
    getInventorySpy.and.returnValue(throwError(() => new Error("Inventory not found")));
    component.ngOnInit();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(router.url).toBe("/error");
  });
  
});
