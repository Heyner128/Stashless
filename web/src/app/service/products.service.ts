import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import type { Product } from '../model/product';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) {}

  getProducts(): Observable<Product[]> {
    return this.httpClient.get<Product[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Product[]>) => {
        return response.body || [];
      })
    );
  }

  getProduct(uuid: string): Observable<Product> {
    return this.httpClient.get<Product>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${uuid}`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Product>) => {
        return response.body || {} as Product;
      })
    );
  }

  createProduct(product: {
    name: string,
    description?: string,
    brand: string
  }): Observable<Product> {
    return this.httpClient.post<Product>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products`,
      product,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Product>) => {
        return response.body || {} as Product;
      })
    );
  }

  updateProduct(uuid: string, product: {
    name: string,
    description?: string,
    brand: string
  }): Observable<Product> {
    return this.httpClient.put<Product>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${uuid}`,
      product,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Product>) => {
        return response.body || {} as Product;
      })
    );
  }

  deleteProduct(uuid: string): Observable<{}> {
    return this.httpClient.delete<{}>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${uuid}`,
      {
        observe: 'response',
      }
    ).pipe(
      map(() => ({}))
    );
  }
}
