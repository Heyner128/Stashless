import { ChangeDetectorRef, Component, CUSTOM_ELEMENTS_SCHEMA, OnInit} from '@angular/core';
import { InventoriesService } from '../../service/inventories.service';
import { Inventory } from '../../model/inventory';
import RelativeTimeElement from '@github/relative-time-element';
import { createAngularTable, createColumnHelper, FlexRenderDirective, getCoreRowModel, Table } from '@tanstack/angular-table';
import { DomSanitizer } from '@angular/platform-browser';
import { Router, RouterLink } from '@angular/router';
import { tap } from 'rxjs';

@Component({
  selector: "app-inventories",
  imports: [FlexRenderDirective, RouterLink],
  templateUrl: "./inventories.component.html",
  styleUrl: "./inventories.component.scss",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class InventoriesComponent implements OnInit {
  private readonly columnHelper = createColumnHelper<Inventory>();
  inventories?: Inventory[];
  
  columns = [
    this.columnHelper.accessor("name", {
      header: "Name",
    }),
    this.columnHelper.accessor("description", {
      header: "Description",
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
  table: Table<Inventory> | undefined;

  constructor(
    private readonly inventoriesService: InventoriesService,
    private readonly router: Router,
    private readonly sanitizer: DomSanitizer,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {
    console.debug(`Loaded web component ${RelativeTimeElement.name}`);
    
  }
  ngOnInit(): void {
    this.inventoriesService.getInventories()
    .subscribe({
      next: (inventories) => {
        this.inventories = inventories;
        this.table = createAngularTable(() => ({
          data: inventories,
          columns: this.columns,
          getCoreRowModel: getCoreRowModel(),
        }));
        this.changeDetectorRef.detectChanges()
      },
      error: () => {
        this.router.navigate(["/error"], {
          skipLocationChange: true,
        });
      },
    });
  }
}

