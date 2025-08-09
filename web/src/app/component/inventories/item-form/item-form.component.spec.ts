import { ComponentFixture, TestBed } from '@angular/core/testing';

import {ItemFormComponent} from './item-form.component';
import { provideHttpClient } from '@angular/common/http';
import {OptionsService} from "../../../service/options.service";
import {Product} from "../../../model/product";
import {Option} from "../../../model/option";
import {User} from "../../../model/user";
import {of} from "rxjs";
import {ProductsService} from "../../../service/products.service";
import {provideRouter} from "@angular/router";
import {NewItem} from "../../../model/item";


const MOCK_USER: User = {
  username: "test_user",
  email: "test@test.com",
  authorities: ["USER"],
};

const MOCK_DATE = new Date(Date.UTC(2022, 1, 1));


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
}];

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


describe('ItemForm', () => {
  let component: ItemFormComponent;
  let fixture: ComponentFixture<ItemFormComponent>;
  let productsService: ProductsService
  let optionsService: OptionsService;

  let optionsSpy: jasmine.Spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemFormComponent],
      providers: [
        provideHttpClient(),
        provideRouter([
          {
            path: "**",
            component: ItemFormComponent,
          },
        ]),
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemFormComponent);
    component = fixture.componentInstance;

    injectDependencies();

    initializeInputs();

    stubProducts();
    stubOptions();
    fixture.detectChanges();
  });

  function injectDependencies() {
    productsService = TestBed.inject(ProductsService);
    optionsService = TestBed.inject(OptionsService);
  }

  function initializeInputs() {
    component.item.set(MOCK_INVENTORY_ITEM);
  }

  function stubProducts() {
    spyOn(productsService, 'getProducts').and.returnValue(of([MOCK_PRODUCT]))
  }

  function stubOptions() {
    optionsSpy = spyOn(optionsService, 'getOptions').and.returnValue(of(MOCK_OPTIONS))
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should a message if there is no options', () => {
    component.item.set({
      ...MOCK_INVENTORY_ITEM,
      options: {}
    });
    component.options.set([]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No options, please add options to the product');
  })
});
