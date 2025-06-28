import { ChangeDetectionStrategy, Component, ElementRef, input, InputSignal, model, signal, viewChild } from '@angular/core';

@Component({
  selector: 'app-multiple-input',
  imports: [],
  templateUrl: './multiple-input.component.html',
  styleUrl: './multiple-input.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultiInputComponent {
  name: InputSignal<string> = input.required<string>();
  values: InputSignal<string[]> = model.required();
  currentInput = signal<string>('');
  inputElement = viewChild<ElementRef<HTMLInputElement>>('input');
  required = input<boolean>(false);


  onInput(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.currentInput.set(inputElement.value);
  }

  addValue() {
    if(this.values().includes(this.currentInput())) return;
    this.values().push(this.currentInput());
    const inputElement = this.inputElement();
    if(inputElement) {
      inputElement.nativeElement.value = "";
    }
    
  }

  removeValue(value: string) {
    const currentValues = this.values();
    const index = currentValues.indexOf(value);
    if (index > -1) {
      this.values().splice(index, 1);
    }
  }
}
