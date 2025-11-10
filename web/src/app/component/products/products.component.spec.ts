import { TestBed } from '@angular/core/testing';

import { ProductsComponent } from './products.component';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { RouterTestingHarness } from '@angular/router/testing';
import { ProductsService } from '../../service/products.service';
import { AuthenticationService } from '../../service/authentication.service';
import { of } from 'rxjs';
import { User } from '../../model/user';
import { Product } from '../../model/product';

const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"],
}

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_PRODUCTS: Product[] = [
  {
    id: "1",
    name: "Product 1",
    description: "Description 1",
    brand: "Brand 1",
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: "2",
    name: "Product 2",
    description: "Description 2",
    brand: "Brand 2",
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];

describe('ProductsComponent', () => {
  let component: ProductsComponent;
  let harness: RouterTestingHarness;
  let router: Router;
  let productsService: ProductsService;
  let authenticationService: AuthenticationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{
          path: '**', component: ProductsComponent
        }])
      ]
    })
    .compileComponents();
    injectDependencies();
    stubAuthentication();
    stubProducts();
    await initializeRouter();
    harness.fixture.autoDetectChanges();
  });

  function injectDependencies() {
    router = TestBed.inject(Router);
    productsService = TestBed.inject(ProductsService);
    authenticationService = TestBed.inject(AuthenticationService);
  }

  function stubAuthentication() {
    spyOn(authenticationService, "isAuthenticated").and.returnValue(true);
  }

  function stubProducts() {
    spyOn(productsService, 'getProducts').and.returnValue(of(MOCK_PRODUCTS));
  }

  async function initializeRouter() {
    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl("/products", ProductsComponent);
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the products names', () => {
    MOCK_PRODUCTS.forEach((product) => {
      expect(harness.routeNativeElement!.textContent).toContain(product.name);
    });
  });

  it('should show the products descriptions', () => {
    MOCK_PRODUCTS.forEach((product) => {
      expect(harness.routeNativeElement!.textContent).toContain(product.description);
    });
  });

  it('should show the products brands', () => {
    MOCK_PRODUCTS.forEach((product) => {
      expect(harness.routeNativeElement!.textContent).toContain(product.brand);
    });
  });

  it('should redirect to /inventories/create when create button is clicked', async () => {
    const createButton: HTMLAnchorElement | null = harness.routeNativeElement!.querySelector('.products__header a');
    expect(createButton).toBeTruthy();
    createButton!.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/products/create');
  });
});
