import { Component, signal, WritableSignal } from '@angular/core';
import { SelectComponent } from "../../select/select.component";
import { OptionsService } from '../../../service/options.service';
import { ProductsService } from '../../../service/products.service';
import { SelectOption } from '../../../model/selectOption';
import { Option } from '../../../model/option';
import { rxResource } from '@angular/core/rxjs-interop';

@Component({
  selector: "app-item-form",
  imports: [SelectComponent],
  templateUrl: "./item-form.html",
  styleUrl: "./item-form.scss",
})
export class ItemForm {
  products = rxResource({
    stream: () => this.productsService.getProducts(),
    defaultValue: [],
  });

  options: WritableSignal<Option[]> = signal([]);

  statusMessage: string | undefined;

  constructor(
    private readonly productsService: ProductsService,
    private readonly optionsService: OptionsService,
  ) {
    
  }

  selectProduct(selectedProductOption: SelectOption) {
    const productUuid = selectedProductOption.value;
    this.optionsService.getOptions(productUuid.toString()).subscribe({
      next: (options) => {
        this.options.set(options);
      },
      error: (message) => (this.statusMessage = message),
    });
  }

  addItem() {
    // This method is a placeholder for adding items to the inventory.
  }
}
