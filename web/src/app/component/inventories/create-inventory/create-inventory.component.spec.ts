import { TestBed } from '@angular/core/testing';

import { CreateInventoryComponent } from './create-inventory.component';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { User } from '../../../../shared/model/user';
import { Inventory } from '../../../model/inventory';
import { RouterTestingHarness } from '@angular/router/testing';
import { InventoriesService } from '../../../service/inventories.service';
import { AuthenticationService } from '../../../../shared/service/authentication.service';
import { of, throwError } from 'rxjs';
import { InputTesting } from '../../../../testing/input';
import { ProductsService } from '../../../service/products.service';
import { NewItem } from '../../../model/item';
import { OptionsService } from '../../../service/options.service';
import { Product } from '../../../model/product';
import { Option } from '../../../model/option';
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
  productUuid: MOCK_PRODUCT.id,
  name: "Item 1",
  amountAvailable: 10,
  costPrice: 100,
  marginPercentage: 20,
  quantity: 1,
  options: {
    [MOCK_OPTIONS[0].name]: MOCK_OPTIONS[0].values[0],
  }
};

describe('CreateInventoryComponent', () => {
  let component: CreateInventoryComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let inventoriesService: InventoriesService;
  let productsService: ProductsService;
  let optionsService: OptionsService;
  let authenticationService: AuthenticationService;
  let inventoriesSpy: jasmine.Spy;

  let nameInput: HTMLInputElement | null;
  let descriptionInput: HTMLTextAreaElement | null;
  let submitButton: HTMLButtonElement | null;
  
  let itemNameInput: HTMLInputElement | null;
  let itemAmountAvailableInput: HTMLInputElement | null;
  let itemCostPriceInput: HTMLInputElement | null;
  let itemMarginPercentageInput: HTMLInputElement | null;
  let itemQuantityInput: HTMLInputElement | null;
  let itemProductSelect: HTMLElement  | undefined;
  let itemOptionSelect: HTMLElement | undefined;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateInventoryComponent],
      providers: [
        provideHttpClient(),
        provideOAuthClient(),
        provideRouter([
          {
            path: "**",
            component: CreateInventoryComponent,
          },
        ]),
      ],
    }).compileComponents();
    injectDependencies();
    stubAuthentication();
    stubInventories();
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
    inventoriesSpy = spyOn(inventoriesService, "createInventory").and.returnValue(of(MOCK_INVENTORY));
  }

  function stubProducts() {
    spyOn(productsService, "getProducts").and.returnValue(of([MOCK_PRODUCT]));
  }

  function stubOptions() {
    spyOn(optionsService, "getOptions").and.returnValue(of(MOCK_OPTIONS));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/inventories", CreateInventoryComponent);
  }

  function searchForInputs() {
    nameInput = harness.routeNativeElement!.querySelector(".inventory-form__fields input[name='name']");
    descriptionInput = harness.routeNativeElement!.querySelector(
      ".inventory-form__fields textarea[name='description']"
    );
    submitButton = harness.routeNativeElement!.querySelector(
      "button[type='submit'].inventory-form__submit "
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
    itemProductSelect = Array.from(harness.routeNativeElement!.querySelectorAll<HTMLElement>(
      "app-select"
    )).at(0);
  }

  function searchForOptionSelect() {
    itemOptionSelect = Array.from(harness.routeNativeElement!.querySelectorAll<HTMLElement>(
      `app-select`
    )).at(-1);
  }

  function selectOption(selectElement: HTMLElement, index: number) {

    const optionElements: NodeList =
      selectElement.querySelectorAll(".select__option");
    
    (optionElements[index] as HTMLDivElement).dispatchEvent(
      new MouseEvent("mousedown", { bubbles: true })
    );
    harness.detectChanges();
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


  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /inventories if the inventory is created successfully', async () => {
    fillForm();
    submitButton?.click();
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(router.url).toBe("/inventories");
  });

  it('should show an error message if the inventory creation fails', async () => {
    inventoriesSpy.and.returnValue(throwError(() => new Error("Failed to create inventory")));
    fillForm();
    submitButton?.click();
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(harness.routeNativeElement?.textContent).toContain("Failed to create inventory");
  });
});
