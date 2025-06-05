import { TestBed } from '@angular/core/testing';

import { InventoriesService } from './inventories.service';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { User } from '../model/user';
import { Inventory } from '../model/inventory';
import { AuthenticationService } from './authentication.service';
import { errorInterceptor } from '../interceptor/error.interceptor';
import { ApiTesting } from '../../testing/api';
import { Item, NewItem } from '../model/item';
import { Option } from '../model/option';
import { SKU } from '../model/sku';

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

const MOCK_PRODUCT_UUID = "product-uuid";

const MOCK_OPTIONS: Option[] = [
  {
    id: "option1",
    name: "Option 1",
    values: ["value1", "value2"]
  },
  {
    id: "option2",
    name: "Option 2",
    values: ["value3", "value4"]
  }
];

const MOCK_SKUS: SKU[] = [
  {
    id: "sku1",
    name: "SKU 1",
    costPrice: 100,
    amountAvailable: 50,
    marginPercentage: 20,
    options: {
      option1: "value1",
      option2: "value3"
    },
    createdAt: MOCK_DATE,
    updatedAt: MOCK_DATE,
  },
  {
    id: "sku2",
    name: "SKU 2",
    costPrice: 200,
    amountAvailable: 30,
    marginPercentage: 25,
    options: {
      option1: "value2",
      option2: "value4"
    },  
    createdAt: MOCK_DATE, 
    updatedAt: MOCK_DATE,
  }
];

const MOCK_INVENTORY_ITEMS: Item[] = [
  {
    sku: MOCK_SKUS[0],
    quantity: 5,
  },
  {
    sku: MOCK_SKUS[1],
    quantity: 10,
  }
];

describe('InventoriesService', () => {
  let service: InventoriesService;
  let authenticationService: AuthenticationService;
  let apiTesting: ApiTesting;
  

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        InventoriesService,
        AuthenticationService,
        provideHttpClient(withInterceptors([errorInterceptor]), withFetch()),
        provideHttpClientTesting(),
      ],
    });
    injectDependencies();
    mockAuthenticationService();
  });

  function injectDependencies() {
    service = TestBed.inject(InventoriesService);
    authenticationService = TestBed.inject(AuthenticationService);
    apiTesting = TestBed.inject(ApiTesting);
  }

  function mockAuthenticationService() {
    spyOn(authenticationService, "getUsername").and.returnValue(
      MOCK_USER.username
    );
  }

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  it("should get the inventories if the API response is ok", () => {
    
    service.getInventories().subscribe((inventories: Inventory[]) => {
      expect(inventories).toEqual(MOCK_INVENTORIES);
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORIES});
  });
  

  it("should return an error if the API response is not ok", () => {
    service.getInventories().subscribe({
      error: (error: Error) => expect(error).toBeDefined()
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });


  it("should get the inventory if the API response is ok", () => {
    service
      .getInventory(MOCK_INVENTORIES[0].id)
      .subscribe((inventory: Inventory) => {
        expect(inventory).toEqual(MOCK_INVENTORIES[0]);
      });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORIES[0]});
  });

  it("should return an error if the API response is not ok", () => {
    service
      .getInventory(MOCK_INVENTORIES[0].id)
      .subscribe({
        error: (error: Error) => expect(error).toBeDefined()
      });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it("should create the inventory if the API response is ok", () => {

    service.createInventory({
      name: MOCK_INVENTORIES[0].name,
      description: MOCK_INVENTORIES[0].description
    }).subscribe((inventory: Inventory) => {
      expect(inventory).toEqual(MOCK_INVENTORIES[0]);
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORIES[0], status: 201});  
  });

  it("should return an error if the API response is not ok", () => {
    service.createInventory({
      name: MOCK_INVENTORIES[0].name,
      description: MOCK_INVENTORIES[0].description
    }).subscribe({
      error: (error: Error) => expect(error).toBeDefined()
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it("should update the inventory if the API response is ok", () => {
    service.updateInventory(
      MOCK_INVENTORIES[0].id,
      {
        name: MOCK_INVENTORIES[0].name,
        description: MOCK_INVENTORIES[0].description
      }
    ).subscribe(
      (inventory: Inventory) => {
        expect(inventory).toEqual(MOCK_INVENTORIES[0]);
      }
    );
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORIES[0]});
  });

  it("should return an error if the API response is not ok", () => {
    service.updateInventory(
      MOCK_INVENTORIES[0].id,
      {
        name: MOCK_INVENTORIES[0].name,
        description: MOCK_INVENTORIES[0].description
      }
    ).subscribe({
      error: (error: Error) => expect(error).toBeDefined()
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });


  it("should delete the inventory if the API response is ok", () => {
    service.deleteInventory(MOCK_INVENTORIES[0].id).subscribe(
      () => {
        expect(true).toBeTruthy();
      }
    );
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORIES[0]});
  });

  it("should return an error if the API response is not ok", () => {
    service.deleteInventory(MOCK_INVENTORIES[0].id).subscribe({
      error: (error: Error) => expect(error).toBeDefined()
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it("should get the inventory items if the API response is ok", () => {
    service.getInventoryItems(MOCK_INVENTORIES[0].id).subscribe((items: Item[]) => {
      expect(items).toEqual(MOCK_INVENTORY_ITEMS);
    });
    apiTesting.expectSuccessfulApiResponse({body: MOCK_INVENTORY_ITEMS});
  });

  it("should return an error if the API response is not ok", () => {
    service.getInventoryItems(MOCK_INVENTORIES[0].id).subscribe({
      error: (error: Error) => expect(error).toBeDefined()
    });
    apiTesting.expectUnsuccessfulApiResponse();
  });

  it("should create an inventory item if the API response is ok", () => {
    const newItem: NewItem  = {
        name: MOCK_INVENTORY_ITEMS[0].sku.name,
        costPrice: MOCK_INVENTORY_ITEMS[0].sku.costPrice,
        amountAvailable: MOCK_INVENTORY_ITEMS[0].sku.amountAvailable,
        marginPercentage: MOCK_INVENTORY_ITEMS[0].sku.marginPercentage,
        options: MOCK_INVENTORY_ITEMS[0].sku.options,
        quantity: MOCK_INVENTORY_ITEMS[0].quantity
    };

    service.createInventoryItem(
      MOCK_INVENTORIES[0].id, MOCK_PRODUCT_UUID, newItem
    ).subscribe((item: Item) => {
      expect(item).toEqual(MOCK_INVENTORY_ITEMS[0]);
    });
    apiTesting.expectSuccessfulApiResponse({body: newItem, status: 201});
  });

  it("should return an error if the item creation api response is not ok", () => {
    const newItem: NewItem = {
      name: MOCK_INVENTORY_ITEMS[0].sku.name,
      costPrice: MOCK_INVENTORY_ITEMS[0].sku.costPrice,
      amountAvailable: MOCK_INVENTORY_ITEMS[0].sku.amountAvailable,
      marginPercentage: MOCK_INVENTORY_ITEMS[0].sku.marginPercentage,
      options: MOCK_INVENTORY_ITEMS[0].sku.options,
      quantity: MOCK_INVENTORY_ITEMS[0].quantity,
    };

    service
      .createInventoryItem(MOCK_INVENTORIES[0].id, MOCK_PRODUCT_UUID, newItem)
      .subscribe({
        next: () => fail('Expected an error, but got item'),
        error: (error: Error) => {
          expect(error).toBeDefined();
        }
      });

    apiTesting.expectUnsuccessfulApiResponse();
  });

  it("it should delete an inventory item if the API response is ok", () => {

    service
      .deleteInventoryItem(
        MOCK_INVENTORIES[0].id,
        MOCK_PRODUCT_UUID,
        MOCK_INVENTORY_ITEMS[0].sku.id
      )
      .subscribe(() => {
        expect(true).toBeTruthy();
      });
    apiTesting.expectSuccessfulApiResponse({status: 204});
  });

  it("should return an error if the inventory item deletion API response is not ok", () => {
    service
      .deleteInventoryItem(
        MOCK_INVENTORIES[0].id,
        MOCK_PRODUCT_UUID,
        MOCK_INVENTORY_ITEMS[0].sku.id
      )
      .subscribe({
        next: () => fail('Expected an error, but got success'),
        error: (error: Error) => {
          expect(error).toBeDefined();
        }
      });
    apiTesting.expectUnsuccessfulApiResponse();
  });
});