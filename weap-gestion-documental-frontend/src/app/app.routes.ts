import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CarguePlanoComponent } from './components/cargue/planos/cargue-plano.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'cargue-plano', component: CarguePlanoComponent },
];
