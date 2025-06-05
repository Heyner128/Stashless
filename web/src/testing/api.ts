import { HttpTestingController } from '@angular/common/http/testing';
import { HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class ApiTesting {

  constructor(private readonly httpTesting: HttpTestingController) {}

  private expectApiRequest() {
    return this.httpTesting.expectOne((req) => {
      return req.url.startsWith(environment.apiBaseUrl)
    });
  }
  
  expectSuccessfulApiResponse(response?: {status?: number, body?: any}) {
    this.expectApiRequest()
    .flush(
      response?.body ?? {}, 
      {
        status: response?.status ?? HttpStatusCode.Ok,
        statusText: HttpStatusCode[response?.status ?? HttpStatusCode.Ok],
      })
  }

  expectUnsuccessfulApiResponse(response?: {status?: number, message?: string}) {
    this.expectApiRequest().flush(
      {
        message: response?.message ?? "Unexpected error",
        errors: [],
      },
      {
        status: response?.status ?? HttpStatusCode.BadRequest,
        statusText: HttpStatusCode[response?.status ?? HttpStatusCode.BadRequest],
      }
    );
  }
}
