import { HttpClient, HttpResponse } from '@angular/common/http';
import {afterNextRender, inject, Injectable, PLATFORM_ID} from '@angular/core';
import { environment } from '../../environments/environment';
import {catchError, map, Observable, of, switchMap, tap} from 'rxjs';
import { User, NewUser } from '../model/user';
import {OAuthService} from "angular-oauth2-oidc";
import {fromPromise} from "rxjs/internal/observable/innerFrom";
import {isPlatformBrowser} from "@angular/common";
import {getOAuthConfig} from "../auth.config";

type UserInfo = {
  sub: string;
  aud: string;
  azp: string;
  auth_time: number;
  iss: string;
  exp: number;
  iat: number;
  nonce: string;
  jti: string;
  sid: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private readonly httpClient: HttpClient,
    private readonly oauthService: OAuthService,
  ) {
    if(isPlatformBrowser(this.platformId)) {
      this.oauthService.configure(getOAuthConfig());
    }
  }

  getUsername(): Observable<string> {
    return of(this.isAuthenticated())
      .pipe(
        switchMap(isAuthenticated => {
          if(isAuthenticated) {
            // TODO when the token expires, loadUserProfiles fails silently, catch the error and logout the user
            return fromPromise(this.oauthService.loadUserProfile())
              .pipe(
                map(
                  (profile) => {
                    return (profile as {info: UserInfo}).info.sub
                  }
                )
              )
          } else {
            this.logout();
            throw new Error("User not found not authenticated")
          }
        })
      )
  }

  isAuthenticated(): boolean {
    return this.oauthService.hasValidAccessToken() && this.oauthService.hasValidIdToken();
  }

  login(): Observable<boolean> {
    return fromPromise(this.oauthService.loadDiscoveryDocumentAndLogin());
  }

  logout(): void {
    this.oauthService.logOut()
  }

  signup(newUser: NewUser): Observable<User> {
    return this.httpClient
      .post(
        `${environment.authUrl}/users`,
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

  getCurrentUser() {
    return this.httpClient
      .get(
        `${environment.authUrl}/users/${this.getUsername()}`,
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
