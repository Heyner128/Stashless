import { Component, InputSignal, model } from '@angular/core';
import { Option } from '../../../model/option';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-option',
  imports: [ReactiveFormsModule],
  templateUrl: './option.component.html',
  styleUrl: './option.component.scss'
})
export class OptionComponent {
  option: InputSignal<Option> = model.required();
  optionForm = new FormGroup({
    optionValue: new FormControl(''),
  });
  component: any;
  addOptionValue() {
    const optionValue = this.optionForm.value.optionValue;
    if (optionValue) {
      this.option().values.push(optionValue);
      this.optionForm.reset();
    }
  }

  removeOptionValue(value: string) {
    const optionValues = this.option().values;
    const index = optionValues.indexOf(value);
    if (index > -1) {
      optionValues.splice(index, 1);
    }
  }
}
