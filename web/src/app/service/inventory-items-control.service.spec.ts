import { TestBed } from '@angular/core/testing';

import { InventoryItemsControlService } from './inventory-items-control.service';

describe('InventoryItemsControlService', () => {
  let service: InventoryItemsControlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InventoryItemsControlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
