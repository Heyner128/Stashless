import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductsService } from '../../../service/products.service';
import { Option } from '../../../model/option';
import { OptionsService } from '../../../service/options.service';
import { switchMap } from 'rxjs';
import { MultiInputComponent } from "../../multiple-input/multiple-input.component";

@Component({
  selector: "app-edit",
  imports: [ReactiveFormsModule, RouterLink, MultiInputComponent],
  templateUrl: "./edit-product.component.html",
  styleUrl: "./edit-product.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditProductComponent implements OnInit {
  productId: string | undefined;
  productForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
    brand: new FormControl(""),
    optionName: new FormControl(""),
  });
  options: WritableSignal<Option[]> = signal([]);
  statusMessage: WritableSignal<string | undefined> = signal(undefined);
  title = "Edit Product";

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly productsService: ProductsService,
    private readonly optionsService: OptionsService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.productId = params.get("id") ?? undefined;
      if (!this.productId) return;
      this.productsService.getProduct(this.productId).subscribe({
        next: (product) => {
          this.statusMessage.set(undefined);
          this.title = `Edit Product ${product.name}`;
          this.productForm.patchValue({
            name: product.name,
            description: product.description,
            brand: product.brand,
          });
          this.optionsService.getOptions(product.id).subscribe({
            next: (options) => {
              this.options.set(options);
            },
            error: () => {
              this.router.navigate(["/error"], {
                skipLocationChange: true,
              });
            },
          });
          this.changeDetectorRef.markForCheck()
        },
        error: () => {
          this.router.navigate(["/error"], {
            skipLocationChange: true,
          });
        },
      });
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
      name: this.productForm.value.optionName,
      values: [],
    });
    this.productForm.patchValue({ optionName: "" });
  }

  update(): void {
    if (
      !this.productId ||
      !this.productForm.value.name ||
      !this.productForm.value.description ||
      !this.productForm.value.brand
    )
      return;
    if(this.options().length === 0) {
      this.statusMessage.set("Product must have at least one option");
      return;
    }
    this.productsService
      .updateProduct(this.productId, {
        name: this.productForm.value.name,
        description: this.productForm.value.description,
        brand: this.productForm.value.brand,
      })
      .pipe(
        switchMap((product) =>
          this.optionsService.updateOptions(product.id, this.options())
        ),
      )
      .subscribe({
        next: () => {
          this.router.navigateByUrl("/products");
        },
        error: (error) => this.statusMessage.set(error.message),
      });
  }

  delete(): void {
    if (!this.productId) return;
    this.productsService.deleteProduct(this.productId).subscribe({
      next: () => {
        this.router.navigate(["/products"]);
      },
      error: (error: Error) => this.statusMessage.set(error.message),
    });
  }

  preventEnterSubmit(event: Event) {
    event.preventDefault();
    return false;
  }
}
