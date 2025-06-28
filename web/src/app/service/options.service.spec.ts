import { TestBed } from '@angular/core/testing';

import { OptionsService } from './options.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Option } from '../model/option';
import { AuthenticationService } from './authentication.service';
import { ApiTesting } from '../../testing/api';

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));

const MOCK_OPTIONS: Option[] = [
  {
    id: '1',
    name: 'Color',
    values: ['Red', 'Blue', 'Green'],
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: '2',
    name: 'Size',
    values: ['Small', 'Medium', 'Large'],
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
];

const MOCK_PRODUCT_UUID = 'product-123';

describe('OptionsService', () => {
  let service: OptionsService;
  let authenticationService: AuthenticationService;
  let apiTesting: ApiTesting;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    injectDependencies();
    mockAuthenticationService();
  });

  function injectDependencies() {
    service = TestBed.inject(OptionsService);
    authenticationService = TestBed.inject(AuthenticationService);
    apiTesting = TestBed.inject(ApiTesting);
  }

  function mockAuthenticationService() {
    spyOn(authenticationService, "getUsername").and.returnValue("test_user");
  }


  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get options for a product', (done) => {
    service.getOptions(MOCK_PRODUCT_UUID).subscribe(options => {
      expect(options).toEqual(MOCK_OPTIONS);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({
      body: MOCK_OPTIONS,
    });
  });

  it('should return an error if the API response is not ok', (done) => {
    service.getOptions(MOCK_PRODUCT_UUID).subscribe({
      next: () => {
        fail('Expected an error, but got options');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should get a specific option for a product', (done) => {
    service
      .getOption(MOCK_PRODUCT_UUID, MOCK_OPTIONS[0].id!)
      .subscribe((option) => {
        expect(option).toEqual(MOCK_OPTIONS[0]);
        done();
      });
    apiTesting.expectSuccessfulApiResponse({
      body: MOCK_OPTIONS[0],
    });
  });

  it('should return an error if the API response for a specific option is not ok', (done) => {
    service
      .getOption(MOCK_PRODUCT_UUID, MOCK_OPTIONS[0].id!)
      .subscribe({
        next: () => {
          fail('Expected an error, but got an option');
          done();
        },
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should create a new option for a product', (done) => {
    const newOption = { name: 'Material', values: ['Cotton', 'Polyester'] };
    service.createOption(MOCK_PRODUCT_UUID, newOption).subscribe((option) => {
      expect(option.name).toEqual(newOption.name);
      expect(option.id).toBeDefined();
      done();
    });
    apiTesting.expectSuccessfulApiResponse({
      body: { ...newOption, id: '3', createdAt: MOCK_DATE, updatedAt: MOCK_DATE },
    });
  });

  it('should return an error if the API response for creating an option is not ok', (done) => {
    const newOption = { name: 'Material', values: ['Cotton', 'Polyester'] };
    service.createOption(MOCK_PRODUCT_UUID, newOption).subscribe({
      next: () => {
        fail('Expected an error, but got an option');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should update an existing option for a product', (done) => {
    const updatedOption = { name: 'Updated Color', values: ['Red', 'Blue', 'Green', 'Yellow'] };
    service.updateOptions(MOCK_PRODUCT_UUID, [updatedOption]).subscribe((options) => {
      expect(options.length).toBe(1);
      expect(options[0].name).toEqual(updatedOption.name);
      expect(options[0].values).toEqual(updatedOption.values);
      done();
    });
    apiTesting.expectSuccessfulApiResponse({
      body: [updatedOption],
    });
  });

  it('should return an error if the API response for updating an option is not ok', (done) => {
    const updatedOption = { name: 'Updated Color', values: ['Red', 'Blue', 'Green', 'Yellow'] };
    service.updateOptions(MOCK_PRODUCT_UUID, [updatedOption]).subscribe({
      next: () => {
        fail('Expected an error, but got updated options');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it('should delete an option for a product', (done) => {
    service.deleteOption(MOCK_PRODUCT_UUID, MOCK_OPTIONS[0].id!).subscribe(() => {
      expect(true).toBeTrue();
      done();
    }
    );
    apiTesting.expectSuccessfulApiResponse({
      body: MOCK_OPTIONS[0],
    });
  });

  it('should return an error if the API response for deleting an option is not ok', (done) => {
    service.deleteOption(MOCK_PRODUCT_UUID, MOCK_OPTIONS[0].id!).subscribe({
      next: () => {
        fail('Expected an error, but got a successful delete');
        done();
      },
      error: (error) => {
        expect(error).toBeTruthy();
        done();
      }
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });
});
