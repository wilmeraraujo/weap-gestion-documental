import { CarguePlano } from '../../../models/cargue/plano/cargue-plano';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { finalize, interval, Subscription, switchMap } from 'rxjs';
import { LoginService } from '../../../services/login.service';
import { CarguePlanoService } from '../../../services/cargue/cargue-plano.service';
import { HttpResponse } from '@angular/common/module.d-CnjH8Dlt';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { CommonListarComponent } from '../../common-listar.component';
import { MatDialog } from '@angular/material/dialog';
import { CarguePlanoFormComponent } from './form/cargue-plano-form.component';

@Component({
  selector: 'app-cargue-plano',
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    MatButtonModule,
    MatPaginator,
    MatCardModule,
    MatDividerModule,
  ],
  templateUrl: './cargue-plano.component.html',
  styleUrl: './cargue-plano.component.css'
})
export class CarguePlanoComponent extends CommonListarComponent<CarguePlano,CarguePlanoService> {

  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  filtro: string = '';
  private pollingSubscription?: Subscription;
  error: any;
  loading = false;

  @ViewChild(MatPaginator) override paginator: MatPaginator;
  mostrarColumnas: string[] = ['nombreArchivo', 'nitPrestador', 'codigoPrestador', 'fechaCargue', 'numeroRegistro', 'usuario', 'erroresEnCargue', 'acciones'];

  constructor(
    service: CarguePlanoService,
    public loginService: LoginService,
    private dialog: MatDialog,
  ) {
    super(service);
    this.titulo = 'Cargue archivos';
  }
  //abrir modal
  abrirModalCrear(): void {
    const dialogRef = this.dialog.open(CarguePlanoFormComponent, {
      width: '500px',
      position: { top: '10%' }, // mueve el modal hacia arriba
      panelClass: 'custom-modal-container',
      data: null
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado) {
        this.calcularRangos();
      }
      console.log("prueba");
    });
  }

  abrirModalEditar(tipo: CarguePlano): void {
    const dialogRef = this.dialog.open(CarguePlanoFormComponent, {
      width: '500px',
      position: { top: '10%' }, // mueve el modal hacia arriba
      panelClass: 'custom-modal-container',
      data: tipo
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado) {
        this.calcularRangos();
      }
    });
  }

  ngOnDestroy(): void {
    this.detenerPolling();
  }

  private estaProcesando(lista: CarguePlano[]): boolean {
    return lista.some(cargue => !cargue.ejecucionTarea || cargue.ejecucionTarea.status !== 'COMPLETED');
  }

  private iniciarPolling(): void {
    if (this.pollingSubscription) return;
    this.pollingSubscription = interval(5000)
      .pipe(
        switchMap(() =>
          this.loginService.isAdmin
            ? this.service.listarPaginas(this.paginaActual.toString(), this.totalPorPagina.toString())
            : this.service.listarPaginasPorUsuario(this.loginService.userName, this.paginaActual.toString(), this.totalPorPagina.toString())
        )
      )
      .subscribe(p => {
        this.lista = p.content;
        this.totalRegistros = p.totalElements;
        this.dataSource.data = this.lista;
        if (!this.estaProcesando(this.lista)) {
          this.detenerPolling();
        }
      });
  }

  private detenerPolling(): void {
    this.pollingSubscription?.unsubscribe();
    this.pollingSubscription = undefined;
  }


  search(term: string): void {
    this.filtro = term.trim();
    this.paginaActual = 0; // reinicia la paginación
    this.calcularRangos(); // vuelve a cargar los datos con el nuevo filtro
  }

}


