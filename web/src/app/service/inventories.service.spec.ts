import { TestBed } from '@angular/core/testing';

import { InventoriesService } from './inventories.service';
import { provideHttpClient } from '@angular/common/http';

describe('InventoriesService', () => {
  let service: InventoriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()],
    });
    service = TestBed.inject(InventoriesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
