import { TestBed } from '@angular/core/testing';

import { EditProductComponent } from './edit-product.component';
import { RouterTestingHarness } from '@angular/router/testing';
import { provideRouter, Router } from '@angular/router';
import { ProductsService } from '../../../service/products.service';
import { AuthenticationService } from '../../../../shared/service/authentication.service';
import { of, throwError } from 'rxjs';
import { User } from '../../../../shared/model/user';
import { provideHttpClient } from '@angular/common/http';
import { ProductsComponent } from '../products.component';
import { ErrorComponent } from '../../error/error.component';
import { InputTesting } from '../../../../testing/input';
import { OptionsService } from '../../../service/options.service';
import {provideOAuthClient} from "angular-oauth2-oidc";

const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"],
};

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_PRODUCT = {
  id: "1",
  name: "Product 1",
  description: "Description 1",
  brand: "Brand 1",
  user: MOCK_USER,
  createdAt: MOCK_DATE,
  updatedAt: MOCK_DATE,
};

const MOCK_OPTIONS = [
  {
    id: "1",
    name: "Color",
    values: ["Red", "Blue", "Green"],
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: "2",
    name: "Size",
    values: ["Small", "Medium", "Large"],
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];


describe('EditProductComponent', () => {
  let component: EditProductComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let productsService: ProductsService;
  let optionsService: OptionsService;

  let authenticationService: AuthenticationService;
  let getProductSpy: jasmine.Spy;
  let updateProductSpy: jasmine.Spy;
  let deleteProductSpy: jasmine.Spy;

  let nameInput: HTMLInputElement | null;
  let descriptionInput: HTMLTextAreaElement | null;
  let brandInput: HTMLInputElement | null;
  let optionNameInput: HTMLInputElement | null;
  let addOptionButton: HTMLButtonElement | null;
  let submitButton: HTMLButtonElement | null;
  let deleteButton: HTMLButtonElement | null;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProductComponent],
      providers: [
        provideRouter([
          {path: "products/:id", component: EditProductComponent},
          {path: "products", component: ProductsComponent},
          {path: "error", component: ErrorComponent},
        ]),
        provideHttpClient(),
        provideOAuthClient()
      ]
    })
    .compileComponents();
    

    injectDependencies();
    stubAuthentication();
    stubProducts();
    stubOptions();
    await initializeRouter();

    component.options.set([]);
    initializeHtmlElements();
    harness.detectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    productsService = TestBed.inject(ProductsService);
    optionsService = TestBed.inject(OptionsService);
    authenticationService = TestBed.inject(AuthenticationService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(true);
  }

  function stubProducts() {
    updateProductSpy = spyOn(productsService, 'updateProduct').and.returnValue(of(MOCK_PRODUCT));
    getProductSpy = spyOn(productsService, 'getProduct').and.returnValue(of(MOCK_PRODUCT));
    deleteProductSpy = spyOn(productsService, 'deleteProduct').and.returnValue(of({}));
  }

  function stubOptions() {
    spyOn(optionsService, 'updateOptions').and.returnValue(of(MOCK_OPTIONS));
    spyOn(optionsService, 'getOptions').and.returnValue(of(MOCK_OPTIONS));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl(`/products/${MOCK_PRODUCT.id}`, EditProductComponent);
  }

  function initializeHtmlElements() {
    nameInput = harness.routeNativeElement!.querySelector('.product-form__input input[name="name"]');
    descriptionInput = harness.routeNativeElement!.querySelector('.product-form__input textarea[name="description"]');
    brandInput = harness.routeNativeElement!.querySelector('.product-form__input input[name="brand"]');
    optionNameInput = harness.routeNativeElement!.querySelector('.options-form__input input[name="name"]');
    addOptionButton = harness.routeNativeElement!.querySelector('.options-form__add');
    submitButton = harness.routeNativeElement!.querySelector('button.product-form__submit');
    deleteButton = harness.routeNativeElement!.querySelector('button.product-form__delete');
  }

  

  async function fillForm() {
    InputTesting.insertText(nameInput!, "Updated Product");
    InputTesting.insertText(descriptionInput!, "Updated Description");
    InputTesting.insertText(brandInput!, "Updated Brand");
    InputTesting.insertText(optionNameInput!, "New Option");
    addOptionButton!.click();
    harness.detectChanges();
    fillOptionValue("any value");
    harness.detectChanges();

    submitButton!.click();

    await harness.fixture.whenStable();
    harness.detectChanges();
  }

  function fillOptionValue(value: string) {
    const optionValue = harness.routeNativeElement!.querySelector(
      "app-multiple-input .values__input"
    ) as HTMLInputElement;
    InputTesting.insertText(optionValue, value);
    const addValueButton = harness.routeNativeElement!.querySelector(
      "app-multiple-input .values__add"
    ) as HTMLButtonElement;
    addValueButton.click();
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with product data', () => {
    expect(nameInput?.value).toBe(MOCK_PRODUCT.name);
    expect(descriptionInput?.value).toBe(MOCK_PRODUCT.description);
    expect(brandInput?.value).toBe(MOCK_PRODUCT.brand);
  });

  it('should redirect to /products if the update is successful', async () => {
    updateProductSpy.and.returnValue(of(MOCK_PRODUCT));
    await fillForm();

    expect(router.url).toBe('/products');
  });

  it('should show an error message if the update fails', async () => {
    const errorMessage = 'Update failed'; 
    updateProductSpy.and.returnValue(throwError(() => new Error(errorMessage)));
    await fillForm();

    expect(harness.routeNativeElement?.textContent).toContain(errorMessage);
  });

  it('should delete the product and redirect to /products', async () => {
    deleteButton!.click();
    await harness.fixture.whenStable();

    expect(router.url).toBe('/products');
  });

  it('should show an error message if the delete fails', async () => {
    const errorMessage = 'Delete failed';
    deleteProductSpy.and.returnValue(throwError(() => new Error(errorMessage)));
    deleteButton!.click();

    harness.detectChanges();
    await harness.fixture.whenStable();
    

    expect(harness.routeNativeElement?.textContent).toContain(errorMessage);
  });

  it('should redirect to error page if the product is not found', async () => {
    getProductSpy.and.returnValue(throwError(() => new Error('Product not found')));
    component.ngOnInit();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(router.url).toBe('/error');
  });
});
