import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, map, Observable, of } from 'rxjs';
import { NewUser } from '../model/newUser';
import { User } from '../model/user';

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
      catchError((_: Error) => of({ok: false})),
      map(response => response.ok)
    )
  }

  login(username: string, password: string): Observable<User> {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users/login`,
        { username, password },
        { 
          observe: 'response'
        }
      ).pipe(
        map(
          response => response.body as User
        )
      )
  }

  logout(): Observable<{}> {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users/${this.getUsername()}/logout`,
        {},
        {
          observe: 'response',
        }
      ).pipe(
        map(
          () => ({}) 
        )
      )
  }

  signup(newUser: NewUser): Observable<User> {
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/users`,
        newUser,
        {
          observe: 'response',
        }
      )
      .pipe(
        map(
          response => response.body as User
        )
      );
  }
}
