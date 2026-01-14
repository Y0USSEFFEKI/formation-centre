import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

import { API_BASE_URL } from './api.config';
import { AuthRequest, AuthResponse, RegisterRequest } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'ff_token';
  private readonly rolesKey = 'ff_roles';
  private readonly fullnameKey = 'ff_fullname';
  private fullnameSubject = new BehaviorSubject<string>(this.fullname);

  // Observable consumers can subscribe to fullname changes
  get fullname$() {
    return this.fullnameSubject.asObservable();
  }

  constructor(private http: HttpClient) {}

  login(payload: AuthRequest) {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/login`, payload)
      .pipe(
        tap((resp) => {
          if (resp.token) {
            localStorage.setItem(this.tokenKey, resp.token);
          } else {
            localStorage.removeItem(this.tokenKey);
          }

          const roles = Array.isArray(resp.roles) ? resp.roles : [];
          const normalized = roles.map((role) => this.normalizeRole(String(role)));
          localStorage.setItem(this.rolesKey, JSON.stringify(normalized));
          localStorage.setItem(this.fullnameKey, resp.fullname || '');
          if (this.fullnameSubject) {
            this.fullnameSubject.next(resp.fullname || '');
          }
        })
      );
  }

  signup(payload: RegisterRequest) {
    return this.http.post<void>(`${API_BASE_URL}/auth/register`, payload);
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.rolesKey);
    localStorage.removeItem(this.fullnameKey);
    if (this.fullnameSubject) {
      this.fullnameSubject.next('');
    }
  }

  get token(): string | null {
    const raw = localStorage.getItem(this.tokenKey);
    if (!raw || raw === 'null' || raw === 'undefined') {
      return null;
    }
    if (raw.split('.').length !== 3) {
      return null;
    }
    return raw;
  }

  get roles(): string[] {
    const raw = localStorage.getItem(this.rolesKey);
    if (!raw) {
      return [];
    }
    try {
      const parsed = JSON.parse(raw) as string[];
      if (!Array.isArray(parsed)) {
        return [];
      }
      return parsed.map((role) => this.normalizeRole(String(role)));
    } catch {
      localStorage.removeItem(this.rolesKey);
      return [];
    }
  }

  get fullname(): string {
    return localStorage.getItem(this.fullnameKey) || '';
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  hasRole(role: string): boolean {
    return this.roles.includes(this.normalizeRole(role));
  }

  private normalizeRole(role: string): string {
    const trimmed = role.trim();
    if (!trimmed) {
      return trimmed;
    }
    return trimmed.startsWith('ROLE_') ? trimmed : `ROLE_${trimmed}`;
  }
}
