import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { API_BASE_URL } from './api.config';
import { Cours, Specialite } from './models';

@Injectable({ providedIn: 'root' })
export class CatalogueService {
  constructor(private http: HttpClient) {}

  getSpecialites() {
    return this.http.get<Specialite[]>(`${API_BASE_URL}/catalogue/specialites`);
  }

  getCours(specialiteId?: number) {
    let params = new HttpParams();
    if (specialiteId) {
      params = params.set('specialiteId', specialiteId.toString());
    }
    return this.http.get<Cours[]>(`${API_BASE_URL}/catalogue/cours`, { params });
  }
}
