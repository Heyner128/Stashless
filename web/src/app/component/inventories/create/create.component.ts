import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { InventoriesService } from '../../../service/inventories.service';
import { Router } from '@angular/router';
import { DialogRef } from '@angular/cdk/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../../../model/apiError';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-create',
  imports: [ReactiveFormsModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.scss'
})
export class CreateComponent {

  inventoryForm = new FormGroup({
    name: new FormControl(''),
  });

  statusMessage: string | undefined;

  constructor(
    private readonly inventoriesService: InventoriesService,
    public dialogRef: DialogRef
  ) {
  }

  create() {
    if (!this.inventoryForm.value.name) return;
    this.inventoriesService
      .createInventory(this.inventoryForm.value.name)
      .pipe(
        catchError(
          error => {
            if (error instanceof HttpErrorResponse) {
              const errorResponse = error.error as ApiError;
              this.statusMessage = errorResponse.message;
            } else {
              this.statusMessage = 'Error creating inventory';
            }
            return of();
          }
        )
      )
      .subscribe(
        () => {
            this.dialogRef.close();
            window.location.reload();
        }
      )
  }

}
