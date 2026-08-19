import { Routes } from '@angular/router';
import { MunicipioList } from './features/municipios/municipio-list/municipio-list';
import { MunicipioForm } from './features/municipios/municipio-form/municipio-form';
import { MunicipioDetailPage } from './features/municipios/municipio-detail/municipio-detail';
import { AtividadesKanban } from './features/atividades/atividades-kanban/atividades-kanban';
import { FasesPadrao } from './features/fases-padrao/fases-padrao';
import { Equipes } from './features/equipes/equipes';
import { MatrizStatus } from './features/matriz-status/matriz-status';
import { Agendas } from './features/agendas/agendas';

export const routes: Routes = [
  { path: '', component: MunicipioList },
  { path: 'atividades', component: AtividadesKanban },
  { path: 'matriz-status', component: MatrizStatus },
  { path: 'agendas', component: Agendas },
  { path: 'fases-padrao', component: FasesPadrao },
  { path: 'equipes', component: Equipes },
  { path: 'municipios/novo', component: MunicipioForm },
  { path: 'municipios/:id/editar', component: MunicipioForm },
  { path: 'municipios/:id', component: MunicipioDetailPage },
  { path: '**', redirectTo: '' }
];
