import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable } from 'rxjs';

import { AuthService } from '../core/auth.service';
import { StudentService } from '../core/student.service';
import { EtudiantProfile } from '../core/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  profile$: Observable<EtudiantProfile | null>;
  menuOpen = false;
  role: string = '';
  private destroyRef = inject(DestroyRef);

  constructor(
    private student: StudentService,
    private auth: AuthService,
    private router: Router
  ) {
    this.profile$ = this.student.profile$;
  }

  ngOnInit() {
    this.student.loadProfile().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();

    // Use AuthService to obtain normalized roles (centralized logic)
    const roles = this.auth.roles;
    this.role = Array.isArray(roles) ? roles.join(',') : '';
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.auth.logout();
    // Clear cached student profile so UI resets immediately
    this.student.clearProfile();
    this.router.navigate(['/login']);
  }

  initials(prenom?: string, nom?: string, fallback = ''): string {
    if (prenom || nom) {
      const first = prenom ? prenom.trim().charAt(0) : '';
      const last = nom ? nom.trim().charAt(0) : '';
      return (first + last).toUpperCase() || '?';
    }

    const parts = fallback.trim().split(/\s+/);
    if (!parts.length) {
      return '?';
    }
    const first = parts[0]?.charAt(0) || '';
    const last = parts.length > 1 ? parts[parts.length - 1]?.charAt(0) : '';
    return (first + last).toUpperCase() || '?';
  }

  fullnameFallback(): string {
    return this.auth.fullname;
  }
}
