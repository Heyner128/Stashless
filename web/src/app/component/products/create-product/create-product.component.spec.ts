import { TestBed } from '@angular/core/testing';

import { CreateProductComponent } from './create-product.component';
import { User } from '../../../model/user';
import { RouterTestingHarness } from '@angular/router/testing';
import { ProductsService } from '../../../service/products.service';
import { provideRouter, Router } from '@angular/router';
import { AuthenticationService } from '../../../service/authentication.service';
import { OptionsService } from '../../../service/options.service';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { InputTesting } from '../../../../testing/input';

const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"]
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

const MOCK_OPTIONS = [{
  id: '1',
  name: 'Color',
  values: ['Red', 'Blue', 'Green'],
  createdAt: MOCK_DATE,
  updatedAt: MOCK_DATE,
}, {
  id: '2',
  name: 'Size',
  values: ['Small', 'Medium', 'Large'],
  createdAt: MOCK_DATE,
  updatedAt: MOCK_DATE,
}]

describe('CreateProductComponent', () => {
  let component: CreateProductComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let productsService: ProductsService;
  let authenticationService: AuthenticationService;
  let optionsService: OptionsService;
  let productsSpy: jasmine.Spy;

  let nameInput: HTMLInputElement | null;
  let descriptionInput: HTMLTextAreaElement | null;
  let brandInput: HTMLInputElement | null;
  let optionsInput: HTMLInputElement | null;
  let optionsAddButton: HTMLButtonElement | null;
  let submitButton: HTMLButtonElement | null;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateProductComponent],
      providers: [
        provideRouter([{
          path: '**', component: CreateProductComponent
        }]),
        provideHttpClient(),
      ]
    })
    .compileComponents();

    injectDependencies();
    stubAuthentication();
    stubProducts();
    stubOptions();
    await initializeRouter();
    searchForInputs();
    harness.detectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    productsService = TestBed.inject(ProductsService);
    optionsService = TestBed.inject(OptionsService);
    authenticationService = TestBed.inject(AuthenticationService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, 'isAuthenticated').and.returnValue(of(true));
  }

  function stubProducts() {
    productsSpy = spyOn(productsService, 'createProduct').and.returnValue(of(MOCK_PRODUCT));
  }

  function stubOptions() {
    spyOn(optionsService, 'createOption').and.returnValue(of(MOCK_OPTIONS[0]));
  }

  function searchForInputs()  {
    nameInput = document.querySelector('.product-form__input input[name="name"]');
    descriptionInput = document.querySelector('.product-form__input textarea[name="description"]');
    brandInput = document.querySelector('.product-form__input input[name="brand"]');
    optionsInput = document.querySelector('.options-form__input input[name="name"]');
    optionsAddButton = document.querySelector('.options-form__add');
    submitButton = document.querySelector('button.product-form__submit');
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl('/products/create', CreateProductComponent);
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /products if the api response is ok', async () => {
    InputTesting.insertText(nameInput!, MOCK_PRODUCT.name);
    InputTesting.insertText(descriptionInput!, MOCK_PRODUCT.description);
    InputTesting.insertText(brandInput!, MOCK_PRODUCT.brand);

    submitButton!.click();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(router.url).toBe('/products');
  });

  it('should redirect to /products if the api response is ok and options are created', async () => {
    InputTesting.insertText(nameInput!, MOCK_PRODUCT.name);
    InputTesting.insertText(descriptionInput!, MOCK_PRODUCT.description);
    InputTesting.insertText(brandInput!, MOCK_PRODUCT.brand);
    InputTesting.insertText(optionsInput!, MOCK_OPTIONS[0].name);
    optionsAddButton!.click();
    submitButton!.click();
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(router.url).toBe('/products');
  });

  it('should show an error message if the api response is not ok', async () => {
    productsSpy.and.returnValue(throwError(() => new Error('Error creating product')));
    InputTesting.insertText(nameInput!, MOCK_PRODUCT.name);
    InputTesting.insertText(descriptionInput!, MOCK_PRODUCT.description);
    InputTesting.insertText(brandInput!, MOCK_PRODUCT.brand);

    submitButton!.click();
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(harness.routeNativeElement?.textContent).toContain('Error creating product');
  });
});
