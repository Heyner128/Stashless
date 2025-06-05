import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionComponent } from './option.component';

import { Option } from '../../../model/option';

import { InputTesting } from '../../../../testing/input';

const MOCK_OPTION: Option = {
  name: "Color",
  values: ["Red", "Green", "Blue"],
}

describe('OptionComponent', () => {
  let component: OptionComponent;
  let fixture: ComponentFixture<OptionComponent>;

  let valueNameInput: HTMLInputElement | null;
  let addValueButton: HTMLButtonElement | null;
  let removeValueButtons: NodeListOf<HTMLButtonElement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OptionComponent]
    })
    .compileComponents();

    initializeComponent();
    initializeHTMLElements();
    fixture.detectChanges();
  });

  function initializeComponent() {
    fixture = TestBed.createComponent(OptionComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput("option", MOCK_OPTION);
  }

  function initializeHTMLElements() {
    addValueButton = fixture.nativeElement.querySelector('.option-values__add');
    valueNameInput = fixture.nativeElement.querySelector('.option-values__input');
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add the value when the add button is clicked', () => {
    InputTesting.insertText(valueNameInput!, 'Yellow');
    addValueButton!.click();
    fixture.detectChanges();

    expect(component.option().values).toContain('Yellow');
    expect(fixture.nativeElement.innerText).toContain('Yellow');
    expect(valueNameInput?.value).toBe('');
  });

  it('should remove the value when the remove button is clicked', () => {
    removeValueButtons = fixture.nativeElement.querySelectorAll('.option-values__item button');
    const firstOptionValue = MOCK_OPTION.values[0];
    removeValueButtons[0].click();
    fixture.detectChanges();

    expect(component.option().values).not.toContain(firstOptionValue);
    expect(fixture.nativeElement.innerText).not.toContain(firstOptionValue);
  });
});
