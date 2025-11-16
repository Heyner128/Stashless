import { TestBed } from '@angular/core/testing';

import { ProductsService } from './products.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthenticationService } from '../../shared/service/authentication.service';
import { ApiTesting } from '../../testing/api';
import { User } from '../../shared/model/user';
import { Product } from '../model/product';
import {provideOAuthClient} from "angular-oauth2-oidc";

const MOCK_USER: User = {
  username: 'test_user',
  email: 'test&test.com',
  authorities: ['USER'],
};

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_PRODUCTS: Product[] = [
  {
    id: '1',
    name: 'Product 1',
    description: 'Description 1',
    brand: 'Brand 1',
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: '2',
    name: 'Product 2',
    description: 'Description 2',
    brand: 'Brand 2',
    user: MOCK_USER,
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];

describe('ProductsService', () => {
  let service: ProductsService;
  let authenticationService: AuthenticationService;
  let apiTesting: ApiTesting;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideOAuthClient()
      ]
    });
    injectDependencies();
    mockAuthenticationService();
  });

  function injectDependencies() {
    service = TestBed.inject(ProductsService);
    authenticationService = TestBed.inject(AuthenticationService);
    apiTesting = TestBed.inject(ApiTesting);
  }

  function mockAuthenticationService() {
    spyOn(authenticationService, 'getUsername').and.returnValue('test_user');
  }

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the products if the api response is ok', (done) => {
    service.getProducts().subscribe((products) => {
      expect(products).toEqual(MOCK_PRODUCTS);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_PRODUCTS});
  });

  it('should return an error if the api response is not ok', (done) => {
    service.getProducts().subscribe({
      next: () => {
        fail('expected an error, not products');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      },
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should get the product if the api response is ok', (done) => {
    service.getProduct(MOCK_PRODUCTS[0].id).subscribe((product) => {
      expect(product).toEqual(MOCK_PRODUCTS[0]);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_PRODUCTS[0]});
  });

  it('should return an error if the api response is not ok', (done) => {
    service.getProduct(MOCK_PRODUCTS[0].id).subscribe({
      next: () => {
        fail('expected an error, not product');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      },
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should create the product if the api response is ok', (done) => {
    service.createProduct({
      name: MOCK_PRODUCTS[0].name,
      description: MOCK_PRODUCTS[0].description,
      brand: MOCK_PRODUCTS[0].brand,
    }).subscribe((product) => {
      expect(product).toEqual(MOCK_PRODUCTS[0]);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_PRODUCTS[0]});
  });

  it('should return an error if the api response is not ok', (done) => {
    service.createProduct({
      name: MOCK_PRODUCTS[0].name,
      description: MOCK_PRODUCTS[0].description,
      brand: MOCK_PRODUCTS[0].brand,
    }).subscribe({
      next: () => {
        fail('expected an error, not product');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      },
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should update the product if the api response is ok', (done) => {
    service.updateProduct(MOCK_PRODUCTS[0].id, {
      name: MOCK_PRODUCTS[0].name,
      description: MOCK_PRODUCTS[0].description,
      brand: MOCK_PRODUCTS[0].brand,
    }).subscribe((product) => {
      expect(product).toEqual(MOCK_PRODUCTS[0]);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_PRODUCTS[0]});
  });

  it('should return an error if the api response is not ok', (done) => {
    service.updateProduct(MOCK_PRODUCTS[0].id, {
      name: MOCK_PRODUCTS[0].name,
      description: MOCK_PRODUCTS[0].description,
      brand: MOCK_PRODUCTS[0].brand,
    }).subscribe({
      next: () => {
        fail('expected an error, not product');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      },
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should delete the product if the api response is ok', (done) => {
    service.deleteProduct(MOCK_PRODUCTS[0].id).subscribe(() => {
      expect(true).toBeTruthy();
      done();
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_PRODUCTS[0]});
  });

  it('should return an error if the api response is not ok', (done) => {
    service.deleteProduct(MOCK_PRODUCTS[0].id).subscribe({
      next: () => {
        fail('expected an error, not product');
        done();
      },
      error: (error) => {
        expect(error).toBeDefined();
        done();
      },
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });
});
