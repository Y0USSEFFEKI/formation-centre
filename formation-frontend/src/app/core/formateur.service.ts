import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from './api.config';
import { Groupe, Seance, FormateurProfile, Cours } from './models';

@Injectable({ providedIn: 'root' })
export class FormateurService {
  constructor(private http: HttpClient) {}

  getGroupes() {
    return this.http.get<Groupe[]>(`${API_BASE_URL}/formateur/groupes`);
  }

  getCourses() {
    return this.http.get<Cours[]>(`${API_BASE_URL}/formateur/cours`);
  }

  getSchedule() {
    // Use the emploi-du-temps endpoint which returns SeanceDto with group names
    return this.http.get<Seance[]>(`${API_BASE_URL}/formateur/emploi-du-temps`);
  }

  getProfile() {
    return this.http.get<FormateurProfile>(`${API_BASE_URL}/formateur/me`);
  }

  updateProfile(payload: { prenom: string; nom: string; email: string }) {
    return this.http.put<FormateurProfile>(`${API_BASE_URL}/formateur/me`, payload);
  }

  getGroupStudentsNotes(groupeId: number, coursId: number) {
    return this.http.get<any>(`${API_BASE_URL}/formateur/groupes/${groupeId}/etudiants/notes?coursId=${coursId}`);
  }

  exportGroupStudentsPdf(groupeId: number, coursId: number) {
    return this.http.get(`${API_BASE_URL}/formateur/groupes/${groupeId}/etudiants/notes/pdf?coursId=${coursId}`, {
      responseType: 'blob'
    });
  }
} 
