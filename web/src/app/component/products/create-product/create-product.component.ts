import { Component, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ProductsService } from '../../../service/products.service';
import { Option } from '../../../model/option';
import { Router, RouterLink } from '@angular/router';
import { OptionComponent } from '../option/option.component';
import { OptionsService } from '../../../service/options.service';
import { defaultIfEmpty, merge, switchMap } from 'rxjs';

@Component({
  selector: "app-create",
  imports: [ReactiveFormsModule, RouterLink, OptionComponent],
  templateUrl: "./create-product.component.html",
  styleUrl: "./create-product.component.scss",
})
export class CreateProductComponent {
  productForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
    brand: new FormControl(""),
  });
  optionsForm = new FormGroup({
    name: new FormControl(""),
  });
  options: WritableSignal<Option[]> = signal([]);
  statusMessage?: string;

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
        switchMap(product => 
          merge(
            ...this.options().map(option => 
              this.optionsService.createOption(product.id, option)
            )
          ).pipe(
            defaultIfEmpty(product)
          )
        )
      )
      .subscribe({
        next: () => {
          this.router.navigateByUrl("/products");
        },
        error: (message) => (this.statusMessage = message),
      });
  }

  addToOptionsList() {
    if (
      !this.optionsForm.value.name ||
      this.options()
        .map((opt) => opt.name)
        .includes(this.optionsForm.value.name)
    )
      return;
    this.options().push({
      name: this.optionsForm.value.name,
      values: [],
    });
    this.optionsForm.reset(); 
  }
}
