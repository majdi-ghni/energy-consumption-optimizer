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
  appliancesLink: string = '/appliances/';

  constructor(
    private sessionManagementService: SessionManagementService,
    private authService: AuthService,
    private router: Router,
    private sharedDataService: SharedDataService,
  ) {}

  ngOnInit() {
    this.sharedDataService.getLoggedUser().subscribe((res) => {
      this.loggedUser = res;
      console.log(this.loggedUser?.id);
      if (this.loggedUser) {
        this.appliancesLink = this.appliancesLink.concat(this.loggedUser.id);
      }
    });
  }

  onClick() {
    this.clicked = !this.clicked;
  }

  logout() {
    this.authService.logout();
    this.sessionManagementService.clearSession();
    //localStorage.clear();
    this.router.navigate(['/login']);
  }
}
