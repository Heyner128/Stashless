import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, signal, Signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { InventoriesService } from '../../../service/inventories.service';
import { Inventory } from '../../../model/inventory';
import { Item } from '../../../model/item';
import { ItemForm } from "../item-form/item-form.component";
import { switchMap, merge, concat, forkJoin } from 'rxjs';

@Component({
  selector: "app-details",
  imports: [ReactiveFormsModule, RouterLink, ItemForm],
  templateUrl: "./edit-inventory.component.html",
  styleUrl: "./edit-inventory.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditInventoryComponent implements OnInit {
  inventoryId: string | undefined;
  inventoryForm = new FormGroup({
    name: new FormControl(""),
    description: new FormControl(""),
  });

  inventoryItems: WritableSignal<Item[]> = signal([{} as Item]);

  statusMessage: WritableSignal<string | undefined> = signal(undefined)
  title = "Edit Inventory";

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly inventoriesService: InventoriesService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.inventoryId = params.get("id") ?? undefined;
      if (!this.inventoryId) return;
      this.inventoriesService.getInventory(this.inventoryId).subscribe({
        next: (inventory: Inventory) => {
          this.statusMessage.set(undefined);
          this.title = `Edit Inventory ${inventory.name}`;
          this.inventoryForm.patchValue({
            name: inventory.name,
            description: inventory.description,
          });
          this.changeDetectorRef.detectChanges();
        },
        error: () => {
          this.router.navigate(["/error"], {
            skipLocationChange: true,
          });
        },
      });

      this.inventoriesService.getInventoryItems(this.inventoryId).subscribe({
        next: (items) => {
          this.inventoryItems.set(
            items.map((item) => {
              return {
                uuid: item.uuid,
                name: item.name,
                amountAvailable: item.amountAvailable,
                costPrice: item.costPrice,
                marginPercentage: item.marginPercentage,
                quantity: item.quantity,
                productUuid: item.productUuid,
                options: item.options,
              } as Item;
            })
          );
          this.changeDetectorRef.detectChanges();
        },
        error: () => {
          this.router.navigate(["/error"], {
            skipLocationChange: true,
          });
        },
      });
    });
  }

  update(): void {
    if (!this.inventoryId || !this.inventoryForm.value.name) return;
    this.inventoriesService
      .updateInventory(this.inventoryId, {
        name: this.inventoryForm.value.name,
        description: this.inventoryForm.value.description ?? undefined,
      })
      .pipe(
        switchMap(inventory => 
          forkJoin(
            this.inventoryItems().map((item) => {
              return this.inventoriesService.updateInventoryItem(
                inventory.id,
                {
                  uuid: item.uuid,
                  name: item.name,
                  amountAvailable: item.amountAvailable,
                  costPrice: item.costPrice,
                  marginPercentage: item.marginPercentage,
                  quantity: item.quantity,
                  productUuid: item.productUuid,
                  options: item.options,
                }
                );
              }
            )
          )
        )
      )
      .subscribe({
        next: () => {
          this.router.navigate(["/inventories"]);
        },
        error: (error: Error) => (this.statusMessage.set(error.message)),
      });
  }

  delete(): void {
    if (!this.inventoryId) return;
    this.inventoriesService.deleteInventory(this.inventoryId).subscribe({
      next: () => {
        this.router.navigate(["/inventories"]);
      },
      error: (error: Error) => (this.statusMessage.set(error.message)),
    });
  }

  addItem() {
    this.inventoryItems.update((items) => [
      ...items
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
