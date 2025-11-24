import { Injectable } from '@angular/core';
import { CommonService } from '../common.service';
import { BASE_ENDPOINT } from '../../config/app';
import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../../models/page';
import { CarguePlano } from '../../models/cargue/plano/cargue-plano';

@Injectable({
  providedIn: 'root'
})
export class CarguePlanoService extends CommonService<CarguePlano>{

  protected override  baseEndPoint = BASE_ENDPOINT + '/api/plano-cita/cargue';

  constructor(http: HttpClient) {
    super(http);
  }


  public crearCargue(nitPrestador: string, usuario: string,  file: File): Observable<HttpEvent<any>>{
    const formData = new FormData();
    formData.append('nitPrestador',nitPrestador);
    formData.append('codigoPrestador',usuario);
    formData.append('usuario',usuario);
    formData.append('file',file);
    return this.http.post<any>(this.baseEndPoint + '/procesar',formData,
      {
        reportProgress: true,
        observe: 'events'
      }
    );
  }

  listarCarguesPaginados(page: string, size: string, usuario: string) {
    const params = {page, size, usuario };
    return this.http.get<Page<CarguePlano>>(`${this.baseEndPoint}/paginable`, { params });
  }

  public downloadErrorExcel(identificadorCargue: number, page: string, size: string): Observable<Blob> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get(`${this.baseEndPoint}/error-cargue/${identificadorCargue}`, {
      params: params,
      responseType: 'blob'
    });
  }

  public listarPaginasPorUsuario(usuario: string, page: string, size: string): Observable<any> {
    const params = new HttpParams().set('usuario', usuario).set('page', page).set('size', size);
    return this.http.get<any>(`${this.baseEndPoint}/paginable/usuario`, {
      params: params,
    });
  }
  public listarPaginasPorUsuarioYFiltro(  usuario: string,  term: string,  page: string,  size: string): Observable<any> {
    const params = new HttpParams()
      .set('usuario', usuario)
      .set('term', term)
      .set('page', page)
      .set('size', size);

    return this.http.get<any>(`${this.baseEndPoint}/paginable/search`, { params });
  }


}
