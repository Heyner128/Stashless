import { ChangeDetectionStrategy, Component, model, signal, WritableSignal } from '@angular/core';
import { SelectComponent } from "../../select/select.component";
import { OptionsService } from '../../../service/options.service';
import { ProductsService } from '../../../service/products.service';
import { SelectOption } from '../../../model/selectOption';
import { Option } from '../../../model/option';
import { rxResource } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { NewItem } from '../../../model/item';
import { map, of } from 'rxjs';
import {RouterLink} from "@angular/router";

@Component({
  selector: "app-item-form",
  imports: [SelectComponent, ReactiveFormsModule, RouterLink],
  templateUrl: "./item-form.component.html",
  styleUrl: "./item-form.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ItemForm {
  products = rxResource({
    stream: () => this.productsService.getProducts(),
    defaultValue: [],
  });

  item = model<NewItem>(
    {} as NewItem
  );

  options = rxResource({
    stream: () => {
      if (!this.item().productUuid) {
        return of([]);
      }
      return this.optionsService
        .getOptions(this.item().productUuid)
        .pipe(map((options: Option[]) => {
          return options.map((option) => ({
            ...option,
            values: option.values.map((value) => ({
              id: value.concat(option.id ?? ''),
              optionId: option.id,
              value: value
            }))
          }))
        }))
    },
    defaultValue: []
  });

  statusMessage: WritableSignal<string | undefined> = signal(undefined)

  constructor(
    private readonly productsService: ProductsService,
    private readonly optionsService: OptionsService
  ) {}

  getDefaultOptionValue(option: Omit<Option, 'values'> & {values: {id: string, value: string, optionId: string | undefined}[]}): string | undefined {
    if(!this.item()?.options) return;
    return Object.entries(this.item().options).find(([name, _]) => (
      name === option.name
    ))?.at(1)?.concat(option.id ?? '')
  }

  onNameChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.item.update((item) => ({
      ...item,
      name: inputElement.value,
    }))
  }

  onAmountAvailableChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.item.update((item) => ({
      ...item,
      amountAvailable: parseInt(inputElement.value),
    }));
  }

  onCostPriceChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.item.update((item) => ({
      ...item,
      costPrice: parseFloat(inputElement.value),
    }));
  }

  onMarginPercentageChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.item.update((item) => ({
      ...item,
      marginPercentage: parseFloat(inputElement.value),
    }));
  }

  onQuantityChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.item.update((item) => ({
      ...item,
      quantity: parseInt(inputElement.value),
    }));
  }

  refreshProducts() {
    this.products.reload()
  }

  selectProduct(selectedProductOption: SelectOption) {
    const productUuid = selectedProductOption.value;
    this.item.update((item) => ({
      ...item,
      productUuid: productUuid.toString(),
    }));
    this.optionsService
      .getOptions(productUuid.toString())
      .pipe(
        map((options: Option[]) => {
          return options.map((option) => ({
            ...option,
            values: option.values.map((value) => ({
              id: value.concat(option.id ?? ""),
              optionId: option.id,
              value: value,
            })),
          }));
        })
      )
      .subscribe({
        next: (options) => {
          this.options.set(options);
        },
        error: (error: Error) => this.statusMessage.set(error.message),
      });
  }

  selectOption(selectedOption: SelectOption) {
    const option = this.options.value().find(
      (opt) => selectedOption.value.toString().includes(opt.id!) 
    );
    if (option) {
      this.item.update((item) => ({
        ...item,
        options: {
          ...item.options,
          [option.name]: selectedOption.text,
        },
      }));
    }
  }
}

