import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

import { PERMITIDAS_URLS } from '../config/app';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private keycloakService: KeycloakService, private router: Router, private loginService: LoginService) {}
/*
  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {

    const isAuthenticated = this.keycloakService.isLoggedIn();

    if (!isAuthenticated) {
      return true;
    }

    const refererUrl = document.referrer;
        let isAllowedUrl = false;

        for (const allowedUrl of PERMITIDAS_URLS) {
          if (refererUrl.startsWith(allowedUrl)) {
            isAllowedUrl = true;
            break;
          }
        }
    if (isAllowedUrl){
      return true;
    }
    // Si hay roles requeridos en la configuración de la ruta, verificarlos
    const requiredRoles: string[] = route.data['roles'] || [];
    if (requiredRoles.length > 0) {
      const userRoles = await this.loginService.getUserRoles();
      const hasAccess = requiredRoles.some(role => userRoles.includes(role));
      if (!hasAccess) {
        this.router.navigate(['/']);
        return false;
      }
    }

    return true; // Permitir el acceso
  }
}*/
async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
  const isAuthenticated = await this.keycloakService.isLoggedIn();

  // Permitir acceso anónimo solo si viene desde una URL permitida y NO está autenticado
  const refererUrl = document.referrer || '';
  if (!isAuthenticated && PERMITIDAS_URLS.some(url => refererUrl.startsWith(url))) {
    return true;
  }

  // Si no está autenticado y no es acceso permitido por origen, bloquear
  if (!isAuthenticated) {
  await this.keycloakService.login({
    redirectUri: window.location.origin + state.url
  });
  return false;
}

  // Verificar roles requeridos
  const requiredRoles: string[] = route.data['roles'] || [];
  if (requiredRoles.length > 0) {
    const userRoles = await this.loginService.getUserRoles();
    const hasAccess = requiredRoles.some(role => userRoles.includes(role));
    if (!hasAccess) {
      this.router.navigate(['/']);
      return false;
    }
  }

  return true;
}}
