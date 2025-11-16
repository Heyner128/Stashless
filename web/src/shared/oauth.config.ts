import {AuthConfig} from "angular-oauth2-oidc";
import {environment} from "../environments/environment";

export function getOAuthConfig(): AuthConfig {
  return {
    issuer: environment.authUrl,
    redirectUri: environment.authUrl.replace('/auth', ''),
    clientId: 'stashless',
    responseType: 'code',
    scope: 'openid profile email offline_access',
    showDebugInformation: true
  }
}