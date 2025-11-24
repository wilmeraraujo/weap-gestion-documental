import { inject, Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  adminRoles: string[] = ['itc-admin'];
  isAdmin!: boolean;
  userName: string='';
  constructor(private keycloakService: KeycloakService) {

  }
  async ngOnInit(): Promise<void>{
    try {
      const isLoggedIn = await this.keycloakService.isLoggedIn();
      if(isLoggedIn){
        this.userName = this.keycloakService.getUsername();
        this.getUserRoles();
      }
    } catch (error) {
      console.error('Error al obtener el nombre de usuario o roles:', error);
    }


  }
  getUserRoles(): string[] {
    try {
      const roles = this.keycloakService.getUserRoles();
      this.isAdmin = roles.some(role => this.adminRoles.includes(role));
      return roles;
      } catch (error) {
      console.error('Error al obtener los roles del usuario:', error);
      return [];
    }
  }

  logout(){
    sessionStorage.clear();
    this.keycloakService.logout();
  }
}
