import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AdminComponent } from './admin/admin.component';
import { UserComponent } from './user/user.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'user', component: UserComponent },

  // default route
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // fallback (optional but recommended)
  { path: '**', redirectTo: 'login' }
];
