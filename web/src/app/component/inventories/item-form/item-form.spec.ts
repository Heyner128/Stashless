import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemForm } from './item-form';
import { provideHttpClient } from '@angular/common/http';

describe('ItemForm', () => {
  let component: ItemForm;
  let fixture: ComponentFixture<ItemForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemForm],
      providers: [
        provideHttpClient(),
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
