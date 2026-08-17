import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MunicipioService } from '../../../core/services/municipio.service';
import { EquipeService } from '../../../core/services/equipe.service';
import { Equipe } from '../../../core/models/equipe.model';

@Component({
  selector: 'app-municipio-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './municipio-form.html'
})
export class MunicipioForm {
  private readonly fb = inject(FormBuilder);
  private readonly municipioService = inject(MunicipioService);
  private readonly equipeService = inject(EquipeService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  municipioId = signal<number | null>(null);
  salvando = signal(false);
  erro = signal<string | null>(null);
  equipes = signal<Equipe[]>([]);

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    codigoIbge: [''],
    regiao: [''],
    populacao: [null as number | null],
    patrocinadorExecutivo: [''],
    pontoFocalNome: [''],
    pontoFocalEmail: ['', [Validators.email]],
    pontoFocalTelefone: [''],
    dataInicio: [''],
    observacoes: [''],
    equipeId: [null as number | null],
    parado: [false]
  });

  constructor() {
    this.equipeService.listar().subscribe((equipes) => this.equipes.set(equipes));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.municipioId.set(id);
      this.municipioService.obter(id).subscribe((municipio) => {
        this.form.patchValue({
          nome: municipio.nome,
          codigoIbge: municipio.codigoIbge ?? '',
          regiao: municipio.regiao ?? '',
          populacao: municipio.populacao,
          patrocinadorExecutivo: municipio.patrocinadorExecutivo ?? '',
          pontoFocalNome: municipio.pontoFocalNome ?? '',
          pontoFocalEmail: municipio.pontoFocalEmail ?? '',
          pontoFocalTelefone: municipio.pontoFocalTelefone ?? '',
          dataInicio: municipio.dataInicio ?? '',
          observacoes: municipio.observacoes ?? '',
          equipeId: municipio.equipeId,
          parado: municipio.parado
        });
      });
    }
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.salvando.set(true);
    this.erro.set(null);

    const raw = this.form.getRawValue();
    const request = {
      nome: raw.nome,
      codigoIbge: raw.codigoIbge || undefined,
      regiao: raw.regiao || undefined,
      populacao: raw.populacao ?? undefined,
      patrocinadorExecutivo: raw.patrocinadorExecutivo || undefined,
      pontoFocalNome: raw.pontoFocalNome || undefined,
      pontoFocalEmail: raw.pontoFocalEmail || undefined,
      pontoFocalTelefone: raw.pontoFocalTelefone || undefined,
      dataInicio: raw.dataInicio || undefined,
      observacoes: raw.observacoes || undefined,
      equipeId: raw.equipeId ?? undefined,
      parado: raw.parado
    };

    const id = this.municipioId();
    const request$ = id
      ? this.municipioService.atualizar(id, request)
      : this.municipioService.criar(request);

    request$.subscribe({
      next: (municipio) => this.router.navigate(['/municipios', municipio.id]),
      error: () => {
        this.erro.set('Não foi possível salvar o município. Verifique os dados informados.');
        this.salvando.set(false);
      }
    });
  }
}
