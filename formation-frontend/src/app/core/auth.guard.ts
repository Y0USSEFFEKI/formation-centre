import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const requiredRole = route.data?.['role'];
  if (requiredRole) {
    // Support multiple role formats: array of roles, 'ROLE_A|ROLE_B' string, or single role string
    const rolesWanted: string[] = Array.isArray(requiredRole)
      ? requiredRole
      : (typeof requiredRole === 'string' && requiredRole.includes('|')
        ? requiredRole.split('|')
        : [requiredRole]);

    const allowed = rolesWanted.some((r) => auth.hasRole(r));
    if (!allowed) {
      router.navigate(['/login']);
      return false;
    }
  }

  return true;
};
