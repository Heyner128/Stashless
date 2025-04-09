import { Component, CUSTOM_ELEMENTS_SCHEMA, inject } from '@angular/core';
import { InventoriesService } from '../../service/inventories.service';
import { Inventory } from '../../model/inventory';
import RelativeTimeElement from '@github/relative-time-element';
import { createAngularTable, createColumnHelper, FlexRenderDirective, getCoreRowModel } from '@tanstack/angular-table';
import { HttpResponse } from '@angular/common/http';
import { catchError, map, of } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { Dialog } from '@angular/cdk/dialog';
import { CreateComponent as CreateInventoryComponent } from './create/create.component';

type SafeHtml = {
  changingThisBreaksApplicationSecurity: string;
}

@Component({
  selector: 'app-inventories',
  imports: [FlexRenderDirective, RouterLink],
  templateUrl: './inventories.component.html',
  styleUrl: './inventories.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class InventoriesComponent {
  private readonly columnHelper = createColumnHelper<Inventory>();
  private readonly inventories$ = inject(InventoriesService).getInventories();
  sanitizer = inject(DomSanitizer);
  dialog = inject(Dialog);
  inventories = toSignal(
    this.inventories$.pipe(
      catchError(error => {
        console.error(error);
        return of();
      }),
      map((response: HttpResponse<Inventory[]>) =>
        response.body as Inventory[]
      )
    ),
    { initialValue: undefined }
  );
  columns = [
    this.columnHelper.accessor('name', {
      header: 'Name',
      cell: (cell) =>
        this.sanitizer.bypassSecurityTrustHtml(
          `<a
            class="default-link-style"
            routerLink="/inventories/${cell.row.original.id}"
          >${cell.getValue()}</a>`
        ),
    }),
    this.columnHelper.accessor('createdAt', {
      header: 'Created',
      cell: (cell) =>
        this.sanitizer.bypassSecurityTrustHtml(
          `<relative-time datetime="${cell
            .getValue()}"></relative-time>`
        ),
    }),
    this.columnHelper.accessor('updatedAt', {
      header: 'Updated',
      cell: (cell) => 
        this.sanitizer.bypassSecurityTrustHtml(
          `<relative-time datetime="${cell
            .getValue()}"></relative-time>`
        )
    }),
  ];
  table = createAngularTable(() => ({
    data: this.inventories() ?? [],
    columns: this.columns,
    getCoreRowModel: getCoreRowModel(),
  }));

  constructor() {
    console.debug(`Loaded web component ${RelativeTimeElement.name}`);
  }

  openCreateDialog() {
    this.dialog.open(CreateInventoryComponent);
  }
}

