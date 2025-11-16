import { TestBed } from '@angular/core/testing';

import { EditInventoryComponent } from './edit-inventory.component';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { InventoriesService } from '../../../service/inventories.service';
import { AuthenticationService } from '../../../../shared/service/authentication.service';
import { Inventory } from '../../../model/inventory';
import { User } from '../../../../shared/model/user';
import { of, throwError } from 'rxjs';
import { InputTesting } from '../../../../testing/input';
import { InventoriesComponent } from '../inventories.component';
import { ErrorComponent } from '../../error/error.component';
import { Product } from '../../../model/product';
import { Item, NewItem } from '../../../model/item';
import { Option } from '../../../model/option';
import { ProductsService } from '../../../service/products.service';
import { OptionsService } from '../../../service/options.service';
import {provideOAuthClient} from "angular-oauth2-oidc";


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

const MOCK_PRODUCT: Product = {
  id: "product-123",
  name: "Product 1",
  brand: "Brand 1",
  description: "Description of Product 1",
  createdAt: MOCK_DATE,
  updatedAt: MOCK_DATE,
  user: MOCK_USER,
}

const MOCK_OPTIONS: Option[] = [{
  name: "Color",
  values: ["Red", "Blue", "Green"],
}]

const MOCK_INVENTORY_ITEM: NewItem = {
  name: "Item 1",
  productUuid: MOCK_PRODUCT.id,
  amountAvailable: 10,
  costPrice: 100,
  marginPercentage: 20,
  quantity: 1,
  options: {
    [MOCK_OPTIONS[0].name]: MOCK_OPTIONS[0].values[0],
  }
};

describe('EditInventoryComponent', () => {
  let component: EditInventoryComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let inventoriesService: InventoriesService;
  let authenticationService: AuthenticationService;
  let productsService: ProductsService;
  let optionsService: OptionsService;

  let getInventorySpy: jasmine.Spy;
  let updateInventorySpy: jasmine.Spy;
  let deleteInventorySpy: jasmine.Spy;

  let nameInput: HTMLInputElement | null;
  let descriptionInput: HTMLTextAreaElement | null;
  let submitButton: HTMLButtonElement | null;
  let deleteButton: HTMLButtonElement | null;

  let itemNameInput: HTMLInputElement | null;
  let itemAmountAvailableInput: HTMLInputElement | null;
  let itemCostPriceInput: HTMLInputElement | null;
  let itemMarginPercentageInput: HTMLInputElement | null;
  let itemQuantityInput: HTMLInputElement | null;
  let itemProductSelect: HTMLSelectElement | undefined;
  let itemOptionSelect: HTMLSelectElement | undefined;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditInventoryComponent],
      providers: [provideRouter([
        { path: "inventories/:id", component: EditInventoryComponent },
        { path: "inventories", component: InventoriesComponent },
        { path: "error", component: ErrorComponent },
      ]), provideHttpClient(), provideOAuthClient()]
    })
    .compileComponents();
    injectDependencies();
    stubAuthentication();
    stubInventories();
    stubInventoryItems();
    stubProducts();
    stubOptions();

    await initializeRouter();
    searchForInputs();
    searchForItemInputs();
    harness.detectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    inventoriesService = TestBed.inject(InventoriesService);
    authenticationService = TestBed.inject(AuthenticationService);
    productsService = TestBed.inject(ProductsService);
    optionsService = TestBed.inject(OptionsService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(true);
  }

  function stubInventories() {
    updateInventorySpy = spyOn(inventoriesService, "updateInventory").and.returnValue(of(MOCK_INVENTORY));
    spyOn(inventoriesService, "updateInventoryItem").and.returnValue(of({
      uuid: "item-uuid",
      ...MOCK_INVENTORY_ITEM
    }));
    getInventorySpy = spyOn(inventoriesService, "getInventory").and.returnValue(of(MOCK_INVENTORY));
    deleteInventorySpy = spyOn(inventoriesService, "deleteInventory").and.returnValue(of({}));
  }

  function stubInventoryItems() {
    spyOn(inventoriesService, "getInventoryItems").and.returnValue(of([{
      uuid: "sku-123",
      productUuid: MOCK_PRODUCT.id,
      name: MOCK_INVENTORY_ITEM.name,
      amountAvailable: MOCK_INVENTORY_ITEM.amountAvailable,
      costPrice: MOCK_INVENTORY_ITEM.costPrice,
      marginPercentage: MOCK_INVENTORY_ITEM.marginPercentage,
      options: MOCK_INVENTORY_ITEM.options,
      quantity: MOCK_INVENTORY_ITEM.quantity,
    } as Item]));
  }

  function stubProducts() {
    spyOn(productsService, "getProducts").and.returnValue(of([MOCK_PRODUCT]));
  }

  function stubOptions() {
    spyOn(optionsService, "getOptions").and.returnValue(of(MOCK_OPTIONS));
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
      "button.inventory-form__delete"
    );
    submitButton = harness.routeNativeElement!.querySelector(
      "button.inventory-form__submit"
    );
  }

  function searchForItemInputs() {
    itemNameInput = harness.routeNativeElement!.querySelector(
      "app-item-form input[name='name']"
    );
    itemAmountAvailableInput = harness.routeNativeElement!.querySelector(
      "app-item-form input[name='amountAvailable']"
    );
    itemCostPriceInput = harness.routeNativeElement!.querySelector(
      "app-item-form input[name='costPrice']"
    );
    itemMarginPercentageInput = harness.routeNativeElement!.querySelector(
      "app-item-form input[name='marginPercentage']"
    );
    itemQuantityInput = harness.routeNativeElement!.querySelector(
      "app-item-form input[name='quantity']"
    );
  }

  function searchForProductSelect() {
    itemProductSelect = Array.from(
      harness.routeNativeElement!.querySelectorAll<HTMLElement>("app-select .native-select")
    ).at(0) as HTMLSelectElement;
  }

  function searchForOptionSelect() {
    itemOptionSelect = Array.from(
      harness.routeNativeElement!.querySelectorAll<HTMLElement>(`app-select .native-select`)
    ).at(-1) as HTMLSelectElement;
  }

  
  function fillForm() {
    InputTesting.insertText(nameInput!, MOCK_INVENTORY.name);
    InputTesting.insertText(descriptionInput!, MOCK_INVENTORY.description);
    InputTesting.insertText(itemNameInput!, MOCK_INVENTORY_ITEM.name);
    InputTesting.insertText(itemAmountAvailableInput!, MOCK_INVENTORY_ITEM.amountAvailable.toString());
    InputTesting.insertText(itemCostPriceInput!, MOCK_INVENTORY_ITEM.costPrice.toString());
    InputTesting.insertText(itemMarginPercentageInput!, MOCK_INVENTORY_ITEM.marginPercentage.toString());
    InputTesting.insertText(itemQuantityInput!, MOCK_INVENTORY_ITEM.quantity.toString());
    searchForProductSelect();
    selectOption(itemProductSelect!, 0);
    searchForOptionSelect();
    selectOption(itemOptionSelect!, 0);
  }

  function selectOption(selectElement: HTMLSelectElement, index: number) {
    if (selectElement.options.length > index + 1) {
      selectElement.selectedIndex = index + 1;
      selectElement.dispatchEvent(new Event("selected"));
    }
    harness.detectChanges();
  }

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should initialize the form with inventory data", async () => {
    fillForm();
    expect(nameInput?.value).toBe(MOCK_INVENTORY.name);
    expect(descriptionInput?.value).toBe(MOCK_INVENTORY.description);
    expect(itemNameInput?.value).toBe(MOCK_INVENTORY_ITEM.name);
    expect(itemAmountAvailableInput?.value).toBe(MOCK_INVENTORY_ITEM.amountAvailable.toString());
    expect(itemCostPriceInput?.value).toBe(MOCK_INVENTORY_ITEM.costPrice.toString());
    expect(itemMarginPercentageInput?.value).toBe(MOCK_INVENTORY_ITEM.marginPercentage.toString());
    expect(itemQuantityInput?.value).toBe(MOCK_INVENTORY_ITEM.quantity.toString());
    searchForProductSelect();
    expect(itemProductSelect?.value).toBe(MOCK_PRODUCT.id);
    searchForOptionSelect();
    expect(itemOptionSelect?.value).toBe(MOCK_OPTIONS[0].values[0]);
  });

  it("should redirect to /inventories if the update is successful", async () => {
    updateInventorySpy.and.returnValue(of(MOCK_INVENTORY));
    fillForm();
    submitButton?.click();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(router.url).toBe("/inventories");
  });

  it("should show an error message if the update fails", async () => {
    const errorMessage = "Error updating inventory";
    updateInventorySpy.and.returnValue(throwError(() => new Error(errorMessage)));
    fillForm();
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
