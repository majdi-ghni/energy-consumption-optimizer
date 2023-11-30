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
import { ApplianceService } from '../../services/apliance/appliance.service';
import { Appliance } from '../../model/applicance/appliance';
import { ApplianceUsageType } from '../../model/appliance-usage-type/applianceUsageType';
import { ButtonComponent } from '../../components/button/button.component';
import { Router } from '@angular/router';

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
    ButtonComponent,
  ],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css', '../register/register.component.css'],
})
export class HomepageComponent implements OnInit {
  user: User | null = null;
  city: string = '';
  startTime: DateTime;
  endTime: DateTime;
  appliances: Appliance[] = [];
  selectedDeviceName!: string;
  selectedDevice!: Appliance;
  values: { displayedValue: string; objectValue: Appliance }[] = [];

  constructor(
    private userService: UserService,
    private sessionManagement: SessionManagementService,
    private applianceService: ApplianceService,
    private router: Router,
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
      this.user = res;
      localStorage.setItem('zipCode', this.user.address.zipCode);
      this.loadAppliances();
    });
  }

  loadAppliances() {
    const userId = this.user?.id;
    if (userId) {
      this.applianceService.getAppliances(userId).subscribe((res) => {
        this.appliances = res.filter(
          // display only devices that could be planed
          (a) => a.applianceUsageType == ApplianceUsageType.PLANNED_USE,
        );
        this.appliances.forEach((a) => {
          this.values.push({ displayedValue: a.name, objectValue: a });
        });
      });
    }
  }

  onPlanUsageClick() {
    const userId = this.user?.id;
    if (userId && this.selectedDeviceName) {
      this.applianceService
        .getApplianceByUserIdAndApplianceName(userId, this.selectedDeviceName)
        .subscribe((res) => {
          this.selectedDevice = res;
          if (this.selectedDevice && this.user) {
            this.router.navigateByUrl(
              '/planUsage/'.concat(this.user.id, '/', this.selectedDevice.name),
            );
          }
        });
    }
  }
}
