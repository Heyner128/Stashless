import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { SKU, NewSKU } from '../model/sku';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SkuService {

  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) {}

  getSkus(productUuid: string): Observable<SKU[]> {
    return this.httpClient.get<SKU[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/skus`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<SKU[]>) => {
        return response.body || [];
      })
    );
  }

  getSku(productUuid: string, skuUuid: string): Observable<SKU> {
    return this.httpClient.get<SKU>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/skus/${skuUuid}`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<SKU>) => {
        return response.body || {} as SKU;
      })
    );
  }

  createSku(
    productUuid: string,
    sku: NewSKU
  ): Observable<SKU> {
    return this.httpClient.post<SKU>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/skus`,
      sku,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<SKU>) => {
        return response.body || {} as SKU;
      })
    );
  }

  updateSkus(productUuid: string, skus: Partial<NewSKU>[]): Observable<SKU[]> {
    return this.httpClient.put<SKU[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/skus`,
      skus,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<SKU[]>) => {
        return response.body || {} as SKU[];
      })
    );
  }

  deleteSku(productUuid: string, skuUuid: string): Observable<{}> {
    return this.httpClient
      .delete<void>(
        `${
          environment.apiBaseUrl
        }/users/${this.authenticationService.getUsername()}/products/${productUuid}/skus/${skuUuid}`,
        {
          observe: "response",
        }
      )
      .pipe(map(() => ({})));
  }
}
