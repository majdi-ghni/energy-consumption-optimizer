import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { BackgroundComponent } from '../../components/background/background.component';
import { SelectMenuComponent } from '../../components/select-menu/select-menu.component';
import { ElectricityDataComponent } from '../../components/electricity-data/electricity-data.component';
import { UserService } from '../../services/user/user.service';
import { SessionManagementService } from '../../services/auth/session-management.service';
import { User } from '../../model/user/user';
import { DateTime } from 'luxon';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    CommonModule,
    SideNavComponent,
    BackgroundComponent,
    ElectricityDataComponent,
    ModalBoxComponent,
    SelectMenuComponent,
  ],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css', '../register/register.component.css'],
})
export class HomepageComponent implements OnInit {
  user: User | null = null;
  city: string = '';
  startTime: DateTime;
  endTime: DateTime;

  constructor(
    private userService: UserService,
    private sessionManagement: SessionManagementService,
  ) {
    this.startTime = DateTime.now().startOf('hour').toLocal();
    this.endTime = this.startTime.plus({ hour: 1 });
  }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    let username = this.sessionManagement.getUser().username;
    this.userService.getUserByUsername(username).subscribe((res) => {
      console.log(res);
      this.user = res;
      console.log(this.user.address.zipCode);
      localStorage.setItem('zipCode', this.user.address.zipCode);
    });
  }
}
