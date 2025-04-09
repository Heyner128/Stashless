import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, map, Observable, of } from 'rxjs';
import { NewUser } from '../model/newUser';
import { ApiError } from '../model/apiError';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  constructor(
    private readonly httpClient: HttpClient,
  ) {}

  private getCookie(name: string): string | undefined {
    try {

      const cookies: string[] = document.cookie.split(';') ?? [];  
      return cookies
        .find((cookie) => cookie.startsWith(`${name}=`))
        ?.split('=')[1];
    } catch (error) {
      // document isn't available on SSR context
      if (error instanceof ReferenceError) {
        return undefined;
      } else {
        throw error;
      }
    }
    
  }

  getUsername(): string | undefined {
    return this.getCookie('user_id');
  }

  isAuthenticated(): Observable<boolean> {
    return this.httpClient.get(
      environment.apiBaseUrl + '/users/' + (this.getUsername()),
      {
        observe: 'response'
      }
    ).pipe(
      catchError(errorResponse => of(errorResponse)),
      map(response => response.ok)
    )
  }

  login(username: string, password: string): Observable<HttpResponse<Object>> {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users/login`,
        { username, password },
        { 
          observe: 'response'
        }
      )
  }

  logout() {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users/${this.getUsername()}/logout`,
        {},
        {
          observe: 'response',
        }
      )
  }

  signup(newUser: NewUser): Observable<HttpResponse<Object>> {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users`,
        newUser,
        {
          observe: 'response',
        }
      )
  }
}
