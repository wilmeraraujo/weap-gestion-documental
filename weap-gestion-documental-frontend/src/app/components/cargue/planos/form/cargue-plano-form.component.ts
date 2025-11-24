import { Component, Inject, OnInit } from '@angular/core';
import { CommonFormComponent } from '../../../common-form.component';
import { CarguePlano } from '../../../../models/cargue/plano/cargue-plano';
import { CarguePlanoService } from '../../../../services/cargue/cargue-plano.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginService } from '../../../../services/login.service';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cargue-plano-form',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // 🔹 Angular Material
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatDividerModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatSelectModule,
    MatCheckboxModule
  ],
  templateUrl: './cargue-plano-form.component.html',
  styleUrls: ['./cargue-plano-form.component.css']
})
export class CarguePlanoFormComponent extends CommonFormComponent<CarguePlano,CarguePlanoService> implements OnInit{
  public archivoSeleccionado: File | null = null;
  form: FormGroup;
  esEdicion: boolean = false;
  hasAssociatedParams = false;
  processing: boolean = false;
  public progreso: number = 0;

  constructor(
    private fb: FormBuilder,
    protected override service: CarguePlanoService,
    private loginService: LoginService,
    public dialogRef: MatDialogRef<CarguePlanoFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CarguePlano | null
  ) {
    super(service);
    this.nombreModel = 'Cargue archivo';
    this.model = {} as CarguePlano;

    this.form = this.fb.group({
      id: [null],
      codigo: ['', [Validators.required, Validators.maxLength(10)]],
      descripcion: ['', [Validators.required, Validators.maxLength(100)]]
    });

    this.esEdicion = !!data;
    this.titulo = this.esEdicion ? 'Editar ' + this.nombreModel : 'Nuevo ' + this.nombreModel;
    if (this.esEdicion && data) {
      this.model = { ...data };
      this.form.patchValue(this.model);
    }
  }

  ngOnInit(): void { }

  public seleccionarArchivo(event: Event): void {
    const input = event.target as HTMLInputElement | null;

    const file = input?.files?.[0] ?? null;
    if (!file) {
      this.archivoSeleccionado = null;
      Swal.fire('Error al seleccionar el archivo:', 'No se pudo leer el archivo', 'error');
      return;
    }

    // Validación por extensión (más confiable que mimetype para este caso)
    const nombre = (file.name || '').toLowerCase();
    const isTxt = nombre.endsWith('.txt');
    if (!isTxt) {
      this.archivoSeleccionado = null;
      Swal.fire('Error al seleccionar el archivo:', 'El archivo debe ser de tipo .txt', 'error');
      // opcional: limpiar el input de archivo si quieres
      if (input) input.value = '';
      return;
    }

    // Si todo OK, asigna
    this.archivoSeleccionado = file;
  }

  procesar(){
    console.log("ingreso a boton cargar");
    Swal.fire({
      icon: "success",
      title: "Estamos procesando tu archivo",
      showConfirmButton: false,
      timer: 3000
    });
    this.cerrar();
  }

  guardar(): void {
    if (this.form.invalid) return;

    this.model = this.form.value;
    const usuario = this.loginService.userName; // guardado si se necesita en futuro

    //const operacion = this.esEdicion ? this.update(usuario) : this.create(usuario);
    const operacion = this.esEdicion
      ? this.service.update(this.model, usuario)
      : this.service.create(this.model, usuario);

    operacion.subscribe({
      next: () => {
        const mensaje = this.esEdicion ? 'actualizado' : 'creado';
        Swal.fire('Éxito', `${this.nombreModel} ${mensaje}`, 'success');
        this.dialogRef.close(true);
      },
      error: (err) => this.handleError(err)
    });
  }

  cerrar(): void {
    this.dialogRef.close();
  }

  private handleError(err: any): void {
    if (err.status === 400 && err.error) {
      Object.keys(err.error).forEach(key => {
        if (this.form.get(key)) {
          this.form.get(key)?.setErrors({ error_validacion: err.error[key] });
        }
      });
    } else {
      Swal.fire('Error', 'Ocurrió un error inesperado', 'error');
    }
  }

  clearFieldError(field: string) {
    if (this.error && this.error[field]) {
      const { [field]: _, ...rest } = this.error as any;
      this.error = rest;
    }
  }

}
