import { Injectable } from '@angular/core';
import { Generic } from '../models/generic';
import { BehaviorSubject, Observable } from 'rxjs';
import { Sort } from '@angular/material/sort';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export abstract class CommonService<E extends Generic> {

  protected baseEndPoint: string='';

  private sortStateSource = new BehaviorSubject<Sort | null>(null);

  protected cabeceras: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(protected http: HttpClient) { }

  public listar(): Observable<E[]> {
    return this.http.get<E[]>(this.baseEndPoint);
  }

  public listarPaginas(page: string, size: string): Observable<any>{
    const params = new HttpParams()
    .set('page',page)
    .set('size',size)
    .set('sort', 'id,asc');
    console.log("baseEndPoint: ",this.baseEndPoint);
    return this.http.get<any>(`${this.baseEndPoint}/paginable`, { params: params });
  }

  public ver(id: number | string): Observable<E>{
    return this.http.get<E>(`${this.baseEndPoint}/${id}`);
  }

  public crear(e: E): Observable<E> {
    return this.http.post<E>(this.baseEndPoint, e, { headers: this.cabeceras });
  }

  public create(e: E, userName: string): Observable<E> {
    const params = new HttpParams().set('userName', userName);
    return this.http.post<E>(`${this.baseEndPoint}/create`, e, { params: params, headers: this.cabeceras}
    );
  }

  public editar(e: E): Observable<E> {
    return this.http.put<E>(`${this.baseEndPoint}/${e.id}`,e, { headers: this.cabeceras });
  }

  public update(e: E, userName: string): Observable<E> {
    const params = new HttpParams().set('userName', userName);
    return this.http.put<E>(`${this.baseEndPoint}/update/${e.id}`, e, { params: params, headers: this.cabeceras}
    );
  }

  public eliminar(id: number | string): Observable<void>{
    return this.http.delete<void>(`${this.baseEndPoint}/${id}`);
  }

  public activarInactivar(id: any, userName: string, observacion: string): Observable<any> {
      const params = new HttpParams()
      .set('userName', userName)
      .set('observacion', observacion);
      return this.http.put<any>(`${this.baseEndPoint}/activar-inactivar/${id.id}`,id, { params: params, headers: this.cabeceras });
    }
  public filterByDescripcion(
      descripcion: string,
      page: string,
      size: string
    ): Observable<any> {
      const params = new HttpParams()
        .set('page', page)
        .set('size', size);
      return this.http.get<any>(`${this.baseEndPoint}/filter/${descripcion.trim()}`, { params: params });
    }

  public approveCargue(identificadorCargue: number): Observable<any> {
    const requestPayload = {
      estado: 'aprobado',
      fecha: new Date().toISOString().split('T')[0],
      observacion: '',
      estadoTipificacion: '',
      cargue: {
        id: identificadorCargue
      }
    };

    return this.http.post(`${this.baseEndPoint}/cargue-detalles/create`, requestPayload);
  }

  public returnCargue(identificadorCargue: number, observation: string, type: string,
    destinatario:string,asunto:string,texto:string,
  ): Observable<any> {

    const params = new HttpParams()
    .set('destinatario',destinatario)
    .set('asunto',asunto)
    .set('texto',texto);

    const requestPayload = {
      estado: 'noaprobado',
      fecha: new Date().toISOString().split('T')[0],
      observacion: observation,
      estadoTipificacion: type,
      cargue: {
        id: identificadorCargue
      }
    };

    return this.http.post(`${this.baseEndPoint}/cargue-detalles/create`, requestPayload,{ params: params });
  }

  public obtenerCarguePorId(id: number): Observable<any> {
    return this.http.get(`${this.baseEndPoint}/cargue/${id}`);
  }

  currentSortState = this.sortStateSource.asObservable();
  updateSortState(sortState: Sort) {
      this.sortStateSource.next(sortState);
  }
}
