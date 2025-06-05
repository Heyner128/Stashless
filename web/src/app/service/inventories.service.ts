import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { environment } from '../../environments/environment';
import { map, Observable, switchMap } from 'rxjs';
import type { Inventory } from '../model/inventory';
import { Item, NewItem } from '../model/item';
import { SKU } from '../model/sku';
import { SkuService } from './sku.service';

@Injectable({
  providedIn: "root",
})
export class InventoriesService {
  constructor(
    private readonly httpClient: HttpClient,
    private readonly skuService: SkuService,
    private readonly authenticationService: AuthenticationService
  ) {}

  getInventories(): Observable<Inventory[]> {
    return this.httpClient.get<Inventory[]>(
      `${
        environment.apiBaseUrl
      }/users/${this.authenticationService.getUsername()}/inventory`,
      {
        observe: "response",
      }
    ).pipe(
      map((response: HttpResponse<Inventory[]>) => {
        return response.body || [];
      })
    );
  }

  getInventory(uuid: string): Observable<Inventory> {
    return this.httpClient.get<Inventory>(
      `${
        environment.apiBaseUrl
      }/users/${this.authenticationService.getUsername()}/inventory/${uuid}`,
      {
        observe: "response",
      }
    ).pipe(
      map((response: HttpResponse<Inventory>) => {
        return response.body || {} as Inventory;
      })
    );
  }

  createInventory(inventory: {
    name: string,
    description?: string
  }): Observable<Inventory> {
    return this.httpClient.post<Inventory>(
      `${
        environment.apiBaseUrl
      }/users/${this.authenticationService.getUsername()}/inventory`,
      inventory,
      {
        observe: "response",
      }
    ).pipe(
      map((response: HttpResponse<Inventory>) => {
        return response.body || {} as Inventory;
      })
    );
  }

  updateInventory(
    uuid: string,
    inventory: {
      name: string,
      description?: string
    }  
  ): Observable<Inventory> {
    return this.httpClient.put<Inventory>(
      `${
        environment.apiBaseUrl
      }/users/${this.authenticationService.getUsername()}/inventory/${uuid}`,
      inventory,
      {
        observe: "response",
      }
    ).pipe(
      map((response: HttpResponse<Inventory>) => {
        return response.body || {} as Inventory;
      })
    );
  }

  deleteInventory(
    uuid: string
  ): Observable<{}> {
    return this.httpClient.delete<{}>(
      `${
        environment.apiBaseUrl
      }/users/${this.authenticationService.getUsername()}/inventory/${uuid}`,
      {
        observe: "response",
      }
    ).pipe(
      map(() => ({})),
    );
  }

  getInventoryItems(inventoryUuid: string): Observable<Item[]> {
    return this.httpClient.get<Item[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/inventory/${inventoryUuid}/item`,
      {
        observe: "response",
      }
    ).pipe(
      map((response: HttpResponse<Item[]>) => {
        return response.body || {} as Item[];
      })
    );
  }

  createInventoryItem(
    inventoryUuid: string,
    productUuid: string,
    newInventoryItem: NewItem
  ) {
    return this.skuService.createSku(
      productUuid,
      {
        name: newInventoryItem.name,
        costPrice: newInventoryItem.costPrice,
        amountAvailable: newInventoryItem.amountAvailable,
        marginPercentage: newInventoryItem.marginPercentage,
        options: newInventoryItem.options,
      }
    ).pipe(
      switchMap((sku: SKU) => (
        this.httpClient.post<Item>(
          `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/inventory/${inventoryUuid}/item`,
          {
            skuId: sku.id,
            quantity: newInventoryItem.quantity,
          },
          {
            observe: "response",
          }
        ).pipe(
          map((response: HttpResponse<Item>) => {
            return response.body || {} as Item;
          })
        )
      ))
    );
  }

  deleteInventoryItem(
    inventoryUuid: string,
    productUuid: string,
    skuUuid: string
  ): Observable<{}> {
    return this.skuService.deleteSku(productUuid, skuUuid)
      .pipe(
        switchMap(() => (
          this.httpClient.delete<{}>(
            `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/inventory/${inventoryUuid}/item/${skuUuid}`,
            {
              observe: "response",
            }
          ).pipe(
            map(() => ({}))
          ))
        )
      );
  }
}
