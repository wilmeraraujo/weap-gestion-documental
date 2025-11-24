import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MediaMatcher } from '@angular/cdk/layout';
import { Router, RouterModule, NavigationEnd } from '@angular/router';

// 🧩 Angular Material
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatButtonModule } from '@angular/material/button';

// 🧠 Servicio de autenticación
import { LoginService } from '../../services/login.service';
import { KeycloakService } from 'keycloak-angular';

interface MenuItem {
  name: string;
  route?: string;
  icon?: string;
  roles?: string[];
  children?: MenuItem[];

}

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    // ✅ Angular Material necesario para el template
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    MatMenuModule,
    MatDividerModule,
    MatExpansionModule,
    MatButtonModule,
    MatDividerModule
  ],
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent {
  private keycloakService = inject(KeycloakService);
  mobileQuery: MediaQueryList;
  username: any;
  roles: string[] = [];
  activeRoute: string = '';
  isLoggedIn: boolean;
  showInfo = false;
  userEmail = 'usuario@empresa.com';

  // ✅ Estructura del menú lateral
  menuNav: MenuItem[] = [
    {
      name: "Home",
      icon: "home",
      route: "home",
      roles: ["itc-admin", "itc-capita", "itc-pgp"]
    },
    {
      name: "Cargue plano",
      icon: "receipt_long",
      route: "cargue-plano",
      roles: ["itc-admin", "itc-capita", "itc-pgp"]
    }
  ];

  constructor(
    media: MediaMatcher,
    private loginService: LoginService,
    private router: Router
  ) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
  }

  async ngOnInit(): Promise<void> {

      try {
          this.router.events.subscribe(event => {
        if (event instanceof NavigationEnd) {
          this.activeRoute = event.urlAfterRedirects;
        }
      });

      this.isLoggedIn = await this.keycloakService.isLoggedIn();
      if(this.isLoggedIn){
        const userProfile = await this.keycloakService.loadUserProfile();
        this.username = userProfile.username;
        this.roles = this.keycloakService.getUserRoles(); // Obtener los roles del usuario autenticado.
        this.filtrarMenuPorRoles(); // Filtrar el menú basado en los roles.
      }
      this.loginService.ngOnInit();
    } catch (error) {
      console.error('Error al cargar el perfil del usuario:', error);
    }
  }

  filtrarMenuPorRoles(): void {
    this.menuNav = this.menuNav.filter(menu => this.tieneAcceso(menu));
    this.menuNav.forEach(menu => {
      if (menu.children) {
        menu.children = menu.children.filter(submenu => this.tieneAcceso(submenu));
        menu.children.forEach(submenu => {
          if (submenu.children) {
            submenu.children = submenu.children.filter(child => this.tieneAcceso(child));
          }
        });
      }
    });
  }

  tieneAcceso(nav: MenuItem): boolean {
    if (!nav.roles) return true;
    return nav.roles.some(role => this.roles.includes(role));
  }

  tieneSubmenusAccesibles(nav: MenuItem): boolean {
    return !!nav.children && nav.children.some(submenu => this.tieneAcceso(submenu));
  }

  esRutaActiva(route: string): boolean {
    return this.activeRoute.includes(route);
  }

  onRouteSelected(snav: any) {
    // Si está en modo móvil, cierra el sidenav automáticamente
    if (this.mobileQuery.matches && snav.opened) {
      snav.close();
    }
  }

  logout(): void {
    this.loginService.logout();
  }

  login(): void {
    this.keycloakService.login();
  }
}
