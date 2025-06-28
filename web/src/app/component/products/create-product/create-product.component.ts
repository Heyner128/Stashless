import { ChangeDetectionStrategy, Component, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ProductsService } from '../../../service/products.service';
import { Option } from '../../../model/option';
import { Router, RouterLink } from '@angular/router';
import { OptionsService } from '../../../service/options.service';
import { defaultIfEmpty, merge,switchMap } from 'rxjs';
import { MultiInputComponent } from "../../multiple-input/multiple-input.component";

@Component({
  selector: "app-create",
  imports: [ReactiveFormsModule, RouterLink, MultiInputComponent],
  templateUrl: "./create-product.component.html",
  styleUrl: "./create-product.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateProductComponent {
  productForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
    brand: new FormControl(""),
    optionName: new FormControl(""),
  });
  options: WritableSignal<Option[]> = signal([]);
  statusMessage: WritableSignal<string | undefined> = signal(undefined);

  constructor(
    private readonly productsService: ProductsService,
    private readonly optionsService: OptionsService,
    private readonly router: Router
  ) {}

  create() {
    if (!this.productForm.value.name) return;
    this.productsService
      .createProduct({
        name: this.productForm.value.name,
        description: this.productForm.value.description ?? "",
        brand: this.productForm.value.brand ?? "",
      })
      .pipe(
        switchMap((product) =>
          merge(
            ...this.options().map((option) =>
              this.optionsService
                .createOption(product.id, option)
            )
          ).pipe(
            defaultIfEmpty(product),
          )
        ),
      )
      .subscribe({
        next: () => {
          this.router.navigateByUrl("/products");
        },
        error: (error) => {
          this.statusMessage.set(error.message);
        },
      });
  }

  addToOptionsList() {
    if (
      !this.productForm.value.optionName ||
      this.options()
        .map((opt) => opt.name)
        .includes(this.productForm.value.optionName)
    )
      return;
    this.options().push({
      name: this.productForm.value.optionName ?? "",
      values: [],
    });
    this.productForm.patchValue({ optionName: "" });
  }

  preventEnterSubmit(event: Event) {
    event.preventDefault();
    return false;
  }
}
