import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../core/auth.service';
import { StudentService } from '../core/student.service';
import { FormateurService } from '../core/formateur.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  form: FormGroup;

  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private student: StudentService,
    private formateur: FormateurService
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  submit() {
    if (this.form.invalid || this.loading) {
      return;
    }

    this.loading = true;
    this.error = '';

    const payload = {
      username: this.form.value.username || '',
      password: this.form.value.password || ''
    };

    this.auth.login(payload).subscribe({
      next: () => {
        if (this.auth.hasRole('ROLE_ETUDIANT')) {
          // load student profile so navbar and UI update immediately
          this.student.loadProfile().subscribe({
            next: () => {
              this.router.navigate(['/']);
            },
            error: () => {
              // still navigate even if profile load fails
              this.router.navigate(['/']);
            }
          });
        } else if (this.auth.hasRole('ROLE_FORMATEUR')) {
          // clear any cached student profile so navbar uses fullname fallback
          this.student.clearProfile();
          // optionally prefetch formateur profile (dashboard will fetch too)
          this.formateur.getProfile().subscribe({
            next: () => this.router.navigate(['/']),
            error: () => this.router.navigate(['/'])
          });
        } else {
          this.error = 'Role non supporte pour cette interface.';
          this.auth.logout();
        }
      },
      error: () => {
        this.error = 'Identifiants invalides.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
