import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { SkuService } from './sku.service';
import { NewSKU, SKU } from '../model/sku';
import { AuthenticationService } from './authentication.service';
import { ApiTesting } from '../../testing/api';

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));
const MOCK_PRODUCT_UUID = 'product-123';
const MOCK_USERNAME = 'test_user';

const MOCK_SKUS: SKU[] = [
  {
    id: 'sku-1',
    name: 'SKU Alpha',
    costPrice: 10.99,
    amountAvailable: 100,
    marginPercentage: 20,
    options: {
      color: 'red',
      size: 'M',
    },
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: 'sku-2',
    name: 'SKU Beta',
    costPrice: 15.50,
    amountAvailable: 50,
    marginPercentage: 25,
    options: {
      color: 'blue',
      size: 'L',
    },
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];

describe('SkuService', () => {
  let service: SkuService;
  let httpMock: HttpTestingController;
  let authenticationService: AuthenticationService;
  let apiTesting: ApiTesting;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    injectDependencies();
    mockAuthenticationService();
  });

  function injectDependencies() {
    service = TestBed.inject(SkuService);
    authenticationService = TestBed.inject(AuthenticationService);
    apiTesting = TestBed.inject(ApiTesting);
  }

  function mockAuthenticationService() {
    spyOn(authenticationService, 'getUsername').and.returnValue(MOCK_USERNAME);
  }

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get SKUs for a product', () => {
    service.getSkus(MOCK_PRODUCT_UUID).subscribe((skus) => {
      expect(skus).toEqual(MOCK_SKUS);
    });

    apiTesting.expectSuccessfulApiResponse({body: MOCK_SKUS});
  });

  it('should return an error if the API response for getSkus is not ok', () => {
    service.getSkus(MOCK_PRODUCT_UUID).subscribe({
      next: () => fail('Expected an error, but got SKUs'),
      error: (error) => {
        expect(error).toBeTruthy();
      },
    });

    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should get a specific SKU for a product', () => {
    service.getSku(MOCK_PRODUCT_UUID, MOCK_SKUS[0].id).subscribe(sku => {
      expect(sku).toEqual(MOCK_SKUS[0]);
    });

    apiTesting.expectSuccessfulApiResponse({
      body: MOCK_SKUS[0],
    });
  });

  it('should return an error if the API response for getSku is not ok', () => {
    service.getSku(MOCK_PRODUCT_UUID, 'invalid-sku').subscribe({
      next: () => fail('Expected an error, but got SKU'),
      error: (error) => {
        expect(error).toBeTruthy();
      },
    });

    apiTesting.expectUnsuccessfulApiResponse();
  });


  it('should create a new SKU for a product', () => {
    const newSku: NewSKU
    = {
      name: 'SKU Gamma',
      costPrice: 12.99,
      amountAvailable: 200,
      marginPercentage: 30,
      options: {
        color: 'green',
        size: 'S',
      }
    };

    service.createSku(MOCK_PRODUCT_UUID, newSku).subscribe(sku => {
      expect(sku.name).toBe(newSku.name!);
      expect(sku.costPrice).toBe(newSku.costPrice!);
      expect(sku.amountAvailable).toBe(newSku.amountAvailable!);
      expect(sku.marginPercentage).toBe(newSku.marginPercentage!);
    });

    apiTesting.expectSuccessfulApiResponse({
      body: {
        ...newSku,
        id: 'sku-3',
        createdAt: MOCK_DATE,
        updatedAt: MOCK_DATE,
      },
    });
  });

  it('should return an error if the API response for createSku is not ok', () => {
    const newSku: NewSKU
    = {
      name: 'SKU Delta',
      costPrice: 14.99,
      amountAvailable: 150,
      marginPercentage: 35,
      options: {
        color: 'yellow',
        size: 'XL',
      }
    };

    service.createSku(MOCK_PRODUCT_UUID, newSku).subscribe({
      next: () => fail('Expected an error, but got SKU'),
      error: (error) => {
        expect(error).toBeTruthy();
      },
    });

    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should update an existing SKU for a product', () => {
    const updatedSku: Partial<NewSKU> = {
      name: 'SKU Alpha Updated',
      costPrice: 11.99,
      amountAvailable: 120,
      marginPercentage: 22,
    };

    service.updateSkus(MOCK_PRODUCT_UUID, [updatedSku]).subscribe((skus: SKU[]) => {
      expect(skus.length).toBe(1);
      expect(skus[0].name).toBe(updatedSku.name!);
      expect(skus[0].costPrice).toBe(updatedSku.costPrice!);
      expect(skus[0].amountAvailable).toBe(updatedSku.amountAvailable!);
      expect(skus[0].marginPercentage).toBe(updatedSku.marginPercentage!);
    });

    apiTesting.expectSuccessfulApiResponse({
      body: [
        {
          ...updatedSku,
          id: 'sku-1',
          createdAt: MOCK_DATE,
          updatedAt: MOCK_DATE,
        },
      ],
    });
  });

  it('should return an error if the API response for updateSkus is not ok', () => {
    const updatedSku: Partial<SKU> = {
      name: 'SKU Beta Updated',
      costPrice: 16.50,
      amountAvailable: 60,
      marginPercentage: 28,
    };

    service.updateSkus(MOCK_PRODUCT_UUID, [updatedSku]).subscribe({
      next: () => fail('Expected an error, but got SKUs'),
      error: (error) => {
        expect(error).toBeTruthy();
      },
    });

    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should delete an SKU for a product', () => {
    const skuUuidToDelete = MOCK_SKUS[0].id;

    service.deleteSku(MOCK_PRODUCT_UUID, skuUuidToDelete).subscribe(() => {
      expect(true).toBeTrue();
    });

    apiTesting.expectSuccessfulApiResponse({
      body: {},
    });
  });

  it('should return an error if the API response for deleteSku is not ok', () => {
    service.deleteSku(MOCK_PRODUCT_UUID, 'invalid-sku').subscribe({
      next: () => fail('Expected an error, but got successful delete'),
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse(); 
  });
});
