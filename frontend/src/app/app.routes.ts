import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { HomepageComponent } from './pages/homepage/homepage.component';
import { AuthGuardService } from './services/auth/auth-guard.service';
import { PlanUsageComponent } from './pages/plan-usage/plan-usage.component';
import { AppliancesComponent } from './pages/appliances/appliances.component';
import { UsagePlanesComponent } from './pages/usage-planes/usage-planes.component';

export const routes: Routes = [
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'forgotPassword',
    component: ForgotPasswordComponent,
  },
  {
    path: 'home',
    component: HomepageComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'planUsage/:id/:device',
    component: PlanUsageComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'appliances/:userId',
    component: AppliancesComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'usagePlans/:userId',
    component: UsagePlanesComponent,
    canActivate: [AuthGuardService],
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // Default route
];

@NgModule({
  imports: [RouterModule.forRoot(routes), RouterModule],
  exports: [RouterModule],
})
export class AppRoutingModule {}
