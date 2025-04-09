import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoriesComponent } from './inventories.component';
import { provideHttpClient } from '@angular/common/http';

describe('InventoriesComponent', () => {
  let component: InventoriesComponent;
  let fixture: ComponentFixture<InventoriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoriesComponent],
      providers: [provideHttpClient()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InventoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
