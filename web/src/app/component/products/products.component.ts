import { ChangeDetectionStrategy, ChangeDetectorRef, Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { createAngularTable, createColumnHelper, FlexRenderDirective, getCoreRowModel, Table } from '@tanstack/angular-table';
import { Product } from '../../model/product';
import RelativeTimeElement from '@github/relative-time-element';
import { ProductsService } from '../../service/products.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: "app-products",
  imports: [FlexRenderDirective, RouterLink],
  templateUrl: "./products.component.html",
  styleUrl: "./products.component.scss",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductsComponent implements OnInit {
  private readonly columnHelper = createColumnHelper<Product>();
  products?: Product[];

  columns = [
    this.columnHelper.accessor("name", {
      header: "Name",
    }),
    this.columnHelper.accessor("description", {
      header: "Description",
    }),
    this.columnHelper.accessor("brand", {
      header: "Brand",
    }),
    this.columnHelper.accessor("createdAt", {
      header: "Created",
      cell: (cell) =>
        this.sanitizer.bypassSecurityTrustHtml(
          `<relative-time datetime="${cell.getValue()}"></relative-time>`
        ),
    }),
    this.columnHelper.accessor("updatedAt", {
      header: "Updated",
      cell: (cell) =>
        this.sanitizer.bypassSecurityTrustHtml(
          `<relative-time datetime="${cell.getValue()}"></relative-time>`
        ),
    }),
  ];
  table: Table<Product> | undefined;
  constructor(
    private readonly productsService: ProductsService,
    private readonly router: Router,
    private readonly sanitizer: DomSanitizer,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {
    console.debug(`Loaded web component ${RelativeTimeElement.name}`);
  }

  ngOnInit(): void {
    this.productsService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.table = createAngularTable(() => ({
          data: products,
          columns: this.columns,
          getCoreRowModel: getCoreRowModel(),
        }));
        this.changeDetectorRef.detectChanges();
      },
      error: () => {
        this.router.navigateByUrl("/error", {
          skipLocationChange: true,
        });
      },
    });
  }
}
