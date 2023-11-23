import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SessionManagementService } from '../../services/auth/session-management.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-side-nav',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.css'],
})
export class SideNavComponent {
  clicked: boolean = false;

  constructor(
    private sessionManagementService: SessionManagementService,
    private authService: AuthService,
    private router: Router,
  ) {}

  onClick() {
    this.clicked = !this.clicked;
  }

  logout() {
    this.authService.logout();
    this.sessionManagementService.clearSession();
    this.router.navigate(['/login']);
  }
}
