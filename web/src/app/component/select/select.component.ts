import { Component, model, input, linkedSignal, computed, signal, WritableSignal, output } from '@angular/core';
import { PopoverComponent } from '../popover/popover.component';
import { SelectOption } from '../../model/selectOption';


@Component({
  selector: "app-select",
  imports: [PopoverComponent],
  templateUrl: "./select.component.html",
  styleUrl: "./select.component.scss",
})
export class SelectComponent {
  isOpen = model<boolean>(false);
  objects = input.required<Object[] | string[]>();
  valueAttribute = input<string>("");
  labelAttribute = input<string>("");
  options = linkedSignal<SelectOption[]>(
    () =>
      this.objects()?.map(
        (obj) =>
          ({
            text:
              typeof obj === "string"
                ? obj
                : (obj as any)[this.labelAttribute()],
            value:
              typeof obj === "string"
                ? obj
                : (obj as any)[this.valueAttribute()],
          } as SelectOption)
      ) || []
  );
  select = output<SelectOption>();
  selectedOption: WritableSignal<SelectOption | undefined> = signal(undefined);
  highlightedOptionIndex = linkedSignal<number>(() => {
    if (this.options().length === 0 || this.selectedOption() === undefined) {
      return -1;
    }
    return this.options().findIndex(
      (option) => option.value === this.selectedOption()?.value
    );
  });
  placeholder = input<string>("Select an option");
  required = input<boolean>(false);
  title = computed<string>(() => {
    const selected = this.selectedOption();
    return selected ? selected.text : this.placeholder();
  });

  selectEventHandler(event: Event, option: SelectOption) {
    event.preventDefault();
    this.selectedOption.set(option);
    this.select.emit(option);
    this.isOpen.set(false);
  }

  toggle(isOpen: boolean = !this.isOpen()) {
    this.isOpen.set(isOpen);
  }

  selectHighlighted() {
    if (!this.isOpen()) {
      this.toggle(true);
    } else {
      const highlightedIndex = this.highlightedOptionIndex();
      if (highlightedIndex >= 0 && highlightedIndex < this.options().length) {
        this.selectEventHandler(new MouseEvent("click"), this.options()[highlightedIndex]);
      }
    }
  }

  highlight(index: number) {
    if (index >= this.options().length) {
      this.highlightedOptionIndex.set(0);
    } else if (index < 0) {
      this.highlightedOptionIndex.set(this.options().length - 1);
    } else {
      this.highlightedOptionIndex.set(index);
    }
  }
}
