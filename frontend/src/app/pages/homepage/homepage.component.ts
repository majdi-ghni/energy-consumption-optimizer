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
import { SelectMenu } from '../../model/select-menu';
import { SharedDataService } from '../../services/shared-data/shared-data.service';

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
  selectedDevice!: Appliance;
  values: SelectMenu[] = [];

  constructor(
    private userService: UserService,
    private sessionManagement: SessionManagementService,
    private applianceService: ApplianceService,
    private router: Router,
    private sharedDataService: SharedDataService,
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
      this.sharedDataService.setLoggedUser(this.user);
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
    if (userId && this.selectedDevice) {
      if (this.user) {
        this.sharedDataService.setSelectedAppliance(this.selectedDevice);
        this.router.navigateByUrl(
          '/planUsage/'.concat(this.user.id, '/', this.selectedDevice.name),
        );
      } else {
        console.log('Benutzer nicht gefunden');
      }
    } else {
      console.log('Gerät nicht gefunden!');
    }
  }

  onSelect($event: any) {
    this.selectedDevice = $event.objectValue;
  }
}
