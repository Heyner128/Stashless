import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopoverComponent } from './popover.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('PopoverComponent', () => {
  let component: PopoverComponent;
  let fixture: ComponentFixture<PopoverComponent>;
  let container: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopoverComponent],
      providers: [provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopoverComponent);
    component = fixture.componentInstance;
    container = fixture.nativeElement;
    fixture.componentRef.setInput('isOpen', false);
    fixture.componentRef.setInput('anchorElement', document.createElement('div'));
    fixture.autoDetectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be visible when isOpen is true', async () => {
    fixture.componentRef.setInput('isOpen', true);
    await fixture.whenStable();
    expect(container.hidden).toBe(false);
  });

  it('should not be visible when isOpen is false', async () => {
    fixture.componentRef.setInput('isOpen', false);
    await fixture.whenStable();
    expect(container.hidden).toBe(true);
  });

  it('should close by clicking outside', async () => {
    fixture.componentRef.setInput('isOpen', true);
    document.body.click();
    await fixture.whenStable();
    expect(container.hidden).toBe(true);
  });
});
