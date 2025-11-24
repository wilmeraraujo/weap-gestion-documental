import { EjecucionTarea } from "../../ejecucion-tarea";

export class CarguePlano {
    id: number;
    nombreArchivo?: string;
    nitPrestador?: string;
    codigoPrestador!: string;
    fechaCargue?: Date;
    erroresEnCargue?: boolean;
    ejecucionTarea?: EjecucionTarea;
    numeroRegistro?: String;
    usuario?: string;
    fechaBaja?: string;
}
