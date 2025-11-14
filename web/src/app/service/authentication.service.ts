import { HttpClient, HttpResponse } from '@angular/common/http';
import {afterNextRender, inject, Injectable, PLATFORM_ID} from '@angular/core';
import { environment } from '../environments/environment';
import {catchError, map, Observable, of, switchMap, tap} from 'rxjs';
import { User, NewUser } from '../model/user';
import {OAuthService} from "angular-oauth2-oidc";
import {fromPromise} from "rxjs/internal/observable/innerFrom";
import {isPlatformBrowser} from "@angular/common";
import {getOAuthConfig} from "../oauth.config";

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

  getUsername(): string | undefined {
    return (this.oauthService.getIdentityClaims() as UserInfo)?.sub;
  }

  isAuthenticated(): boolean {
    try {
      return this.oauthService.hasValidAccessToken() && this.oauthService.hasValidIdToken();
    } catch {
      return false;
    }
  }

  login(): Observable<boolean> {
    return fromPromise(this.oauthService.loadDiscoveryDocumentAndLogin());
  }

  logout(): void {
    this.oauthService.logOut()
  }

  getAccessToken(): string {
    return this.oauthService.getAccessToken();
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
