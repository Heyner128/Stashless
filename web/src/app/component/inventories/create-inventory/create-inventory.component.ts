import { Component, Signal, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { InventoriesService } from '../../../service/inventories.service';
import { Router, RouterLink } from '@angular/router';
import { ItemForm } from "../item-form/item-form";
import { Item } from '../../../model/item';

@Component({
  selector: "app-create",
  imports: [ReactiveFormsModule, RouterLink, ItemForm],
  templateUrl: "./create-inventory.component.html",
  styleUrl: "./create-inventory.component.scss",
})
export class CreateInventoryComponent {
  inventoryForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
  });

  inventoryItems: WritableSignal<Signal<Item>[]> = signal([signal({} as Item)]);

  statusMessage: string | undefined;

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
      .subscribe({
        next: () => {
          this.router.navigate(["/inventories"]);
        },
        error: (message) => (this.statusMessage = message),
      });
  }

  addItem() {
    this.inventoryItems.update(
      (items) => [...items, signal({} as Item)],
    )
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
