import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectComponent } from './select.component';

const MOCK_OPTIONS= [
  { id: 1, name: 'Option 1' },
  { id: 2, name: 'Option 2' },
  { id: 3, name: 'Option 3' }
];

describe('SelectComponent', () => {
  let component: SelectComponent;
  let fixture: ComponentFixture<SelectComponent>;
  let trigger: HTMLDivElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('name', 'test-select');
    fixture.componentRef.setInput('objects', MOCK_OPTIONS);
    fixture.componentRef.setInput('valueAttribute', 'id');
    fixture.componentRef.setInput('labelAttribute', 'name');

    trigger = fixture.nativeElement.querySelector('.select');

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the options when trigger is clicked', () => {
    trigger.click();
    fixture.detectChanges();
    const optionElements: NodeList = fixture.nativeElement.querySelectorAll('.select__option');
    expect(optionElements.length).toBe(MOCK_OPTIONS.length);
    optionElements.forEach((optionElement, index) => {
      expect(optionElement.textContent).toContain(MOCK_OPTIONS[index].name);
    });
  });

  it('should change the title when an option is selected', () => {
    trigger.click();
    fixture.detectChanges();
    selectOption(1);
    expect(getSelectTitle()).toBe(MOCK_OPTIONS[1].name);
  });

  function getSelectTitle(): string {
    return fixture.nativeElement.querySelector('.select__title').textContent.trim();
  }

  function selectOption(index: number): void {
    const optionElements: NodeList = fixture.nativeElement.querySelectorAll('.select__option');
    (optionElements[index] as HTMLDivElement).dispatchEvent(new MouseEvent('mousedown', { bubbles: true }));
    fixture.detectChanges();
  }

  it('should close the options when an option is selected', () => {
    trigger.click();
    fixture.detectChanges();
    selectOption(0);
    expect(component.isOpen()).toBeFalse();
  });

  it('should open the options when the enter key is pressed', () => {
    trigger.dispatchEvent(new KeyboardEvent('keyup', { key: 'Enter' }));
    fixture.detectChanges();
    expect(component.isOpen()).toBeTrue();
  });

  it('should close the options when the escape key is pressed', () => {
    trigger.click();
    fixture.detectChanges();
    trigger.dispatchEvent(new KeyboardEvent('keyup', { key: 'Escape' }));
    fixture.detectChanges();
    expect(component.isOpen()).toBeFalse();
  })
});
