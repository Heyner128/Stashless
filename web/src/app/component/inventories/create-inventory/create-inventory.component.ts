import { ChangeDetectionStrategy, Component,  signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { InventoriesService } from '../../../service/inventories.service';
import { Router, RouterLink } from '@angular/router';
import {ItemFormComponent} from "../item-form/item-form.component";
import { NewItem } from '../../../model/item';
import { defaultIfEmpty, forkJoin, switchMap } from 'rxjs';

@Component({
  selector: "app-create",
  imports: [ReactiveFormsModule, RouterLink, ItemFormComponent],
  templateUrl: "./create-inventory.component.html",
  styleUrl: "./create-inventory.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateInventoryComponent {
  inventoryForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
  });

  inventoryItems: WritableSignal<NewItem[]> = signal([{} as NewItem]);

  statusMessage: WritableSignal<string | undefined> = signal(undefined);

  constructor(
    private readonly inventoriesService: InventoriesService,
    private readonly router: Router
  ) {}

  create() {
    if (!this.inventoryForm.value.name) return;
    this.inventoriesService
      .createInventory({
        name: this.inventoryForm.value.name,
        description: this.inventoryForm.value.description ?? "",
      })
      .pipe(
        switchMap((inventory) => 
          forkJoin(
            this.inventoryItems().map((item) =>
              this.inventoriesService.createInventoryItem(
                inventory.id,
                item
              )
            )
          ).pipe(
            defaultIfEmpty(inventory)
          )
        )
      )
      .subscribe({
        next: () => {
          this.router.navigate(["/inventories"]);
        },
        error: (error) => this.statusMessage.set(error.message),
      });
  }

  addItem() {
    this.inventoryItems.update((items) => [
      ...items,
      {} as NewItem
    ]);
  }

  removeItem() {
    this.inventoryItems.update((items) => {
      if (items.length > 0) {
        items.pop();
      }
      return items;
    });
  }
}
