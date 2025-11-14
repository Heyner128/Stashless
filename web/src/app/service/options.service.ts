import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { map, Observable } from 'rxjs';
import { Option } from '../model/option';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class OptionsService {

  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) {}

  getOptions(productUuid: string): Observable<Option[]> {
    return this.httpClient.get<Option[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/options`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Option[]>) => {
        return response.body || [];
      })
    );
  }

  getOption(productUuid: string, optionUuid: string): Observable<Option> {
    return this.httpClient.get<Option>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/options/${optionUuid}`,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Option>) => {
        return response.body || {} as Option;
      })
    );
  }

  createOption(productUuid: string, option: {
    name: string,
    values: string[],
  }): Observable<Option> {
    return this.httpClient.post<Option>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/options`,
      option,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Option>) => {
        return response.body || {} as Option;
      })
    );
  }

  updateOptions(productUuid: string, options: {
    name: string,
    values: string[],
  }[]): Observable<Option[]> {
    return this.httpClient.put<Option[]>(
      `${environment.apiBaseUrl}/users/${this.authenticationService.getUsername()}/products/${productUuid}/options`,
      options,
      {
        observe: 'response',
      }
    ).pipe(
      map((response: HttpResponse<Option[]>) => {
        return response.body || {} as Option[];
      })
    );
  }

  deleteOption(productUuid: string, optionUuid: string): Observable<{}> {
    return this.httpClient
      .delete<void>(
        `${
          environment.apiBaseUrl
        }/users/${this.authenticationService.getUsername()}/products/${productUuid}/options/${optionUuid}`,
        {
          observe: "response",
        }
      )
      .pipe(map(() => ({})));
  }
}
