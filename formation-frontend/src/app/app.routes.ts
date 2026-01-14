import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';
import { LoginComponent } from './auth/login.component';
import { SignupComponent } from './auth/signup.component';
import { StudentLayoutComponent } from './layout/student-layout.component';
import { HomeComponent } from './features/home/home.component';
import { CoursesComponent } from './features/courses/courses.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: '',
    component: StudentLayoutComponent,
    canActivate: [authGuard],
    data: { role: ['ROLE_ETUDIANT','ROLE_FORMATEUR'] },
    children: [
      { path: '', component: HomeComponent },
      { path: 'courses', component: CoursesComponent },
      { path: 'dashboard', component: DashboardComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
