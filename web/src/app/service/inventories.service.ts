import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { environment } from '../../environments/environment';
import {  Observable } from 'rxjs';
import type { Inventory } from '../model/inventory';
import { ApiError } from '../model/apiError';

@Injectable({
  providedIn: 'root'
})
export class InventoriesService {

  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) {}
  
  getInventories(): Observable<HttpResponse<Inventory[]>> {
    return this.httpClient.get<Inventory[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/inventory`,
      {
        observe: 'response'
      }
    )
  }

  createInventory(name: string): Observable<HttpResponse<Inventory>> {
    return this.httpClient.post<Inventory>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/inventory`,
      { name },
      {
        observe: 'response'
      }
    );
  }
}
