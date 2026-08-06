import { Routes } from '@angular/router';
import { MunicipioList } from './features/municipios/municipio-list/municipio-list';
import { MunicipioForm } from './features/municipios/municipio-form/municipio-form';
import { MunicipioDetailPage } from './features/municipios/municipio-detail/municipio-detail';

export const routes: Routes = [
  { path: '', component: MunicipioList },
  { path: 'municipios/novo', component: MunicipioForm },
  { path: 'municipios/:id/editar', component: MunicipioForm },
  { path: 'municipios/:id', component: MunicipioDetailPage },
  { path: '**', redirectTo: '' }
];
