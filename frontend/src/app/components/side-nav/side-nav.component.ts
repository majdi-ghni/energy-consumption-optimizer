import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SessionManagementService } from '../../services/auth/session-management.service';
import { AuthService } from '../../services/auth/auth.service';
import { User } from '../../model/user/user';
import { SharedDataService } from '../../services/shared-data/shared-data.service';

@Component({
  selector: 'app-side-nav',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.css'],
})
export class SideNavComponent implements OnInit {
  clicked: boolean = false;
  loggedUser: User | null = null;
  appliancesLink: string = '';
  usagePlansLink: string = '';
  userId: string | null = null;

  constructor(
    private sessionManagementService: SessionManagementService,
    private authService: AuthService,
    private router: Router,
    private sharedDataService: SharedDataService,
  ) {}

  ngOnInit() {
    this.userId = localStorage.getItem('userId');
    if (this.userId) {
      this.appliancesLink = '/appliances/'.concat(this.userId);
      this.usagePlansLink = '/usagePlans/'.concat(this.userId);
    }
  }

  onClick() {
    this.clicked = !this.clicked;
  }

  logout() {
    this.authService.logout();
    this.sessionManagementService.clearSession();
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
