import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

import { API_BASE_URL } from './api.config';
import { Cours, EtudiantProfile, Note, Seance } from './models';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private profileSubject = new BehaviorSubject<EtudiantProfile | null>(null);
  profile$ = this.profileSubject.asObservable();
  private myCoursesSubject = new BehaviorSubject<Cours[]>([]);
  myCourses$ = this.myCoursesSubject.asObservable();

  constructor(private http: HttpClient) {}

  clearProfile() {
    this.profileSubject.next(null);
  }

  loadProfile() {
    return this.http
      .get<EtudiantProfile>(`${API_BASE_URL}/etudiant/me`)
      .pipe(tap((profile) => this.profileSubject.next(profile)));
  }

  updateProfile(payload: EtudiantProfile) {
    return this.http
      .put<EtudiantProfile>(`${API_BASE_URL}/etudiant/me`, payload)
      .pipe(tap((profile) => this.profileSubject.next(profile)));
  }

  loadMyCourses() {
    return this.http
      .get<Cours[]>(`${API_BASE_URL}/etudiant/cours`)
      .pipe(tap((courses) => this.myCoursesSubject.next(courses)));
  }

  getMyCourses() {
    return this.loadMyCourses();
  }

  getSchedule() {
    return this.http.get<Seance[]>(`${API_BASE_URL}/etudiant/emploi-du-temps`);
  }

  getNotes() {
    return this.http.get<Note[]>(`${API_BASE_URL}/etudiant/notes`);
  }

  enroll(coursId: number) {
    return this.http
      .post<void>(`${API_BASE_URL}/etudiant/inscriptions`, { coursId })
      .pipe(switchMap(() => this.loadMyCourses()));
  }

  cancelEnrollment(coursId: number) {
    return this.http
      .delete<void>(`${API_BASE_URL}/etudiant/inscriptions/${coursId}`)
      .pipe(switchMap(() => this.loadMyCourses()));
  }
}
