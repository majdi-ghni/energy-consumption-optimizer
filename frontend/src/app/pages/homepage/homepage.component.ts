import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { BackgroundComponent } from '../../components/background/background.component';
import { SelectMenuComponent } from '../../components/select-menu/select-menu.component';
import { ElectricityDataComponent } from '../../components/electricity-data/electricity-data.component';
import { DateTime } from 'luxon';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';
import { ApplianceService } from '../../services/apliance/appliance.service';
import { Appliance } from '../../model/applicance/appliance';
import { ApplianceUsageType } from '../../model/appliance-usage-type/applianceUsageType';
import { ButtonComponent } from '../../components/button/button.component';
import { Router } from '@angular/router';
import { SelectMenu } from '../../model/select-menu';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { NavComponent } from '../../components/nav/nav.component';

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
    NavComponent,
  ],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css', '../register/register.component.css'],
})
export class HomepageComponent implements OnInit {
  userId: string | null = null;
  city: string = '';
  startTime: DateTime;
  endTime: DateTime;
  appliances: Appliance[] = [];
  selectedDevice!: Appliance;
  values: SelectMenu[] = [];

  constructor(
    private applianceService: ApplianceService,
    private router: Router,
    private sharedDataService: SharedDataService,
  ) {
    this.startTime = DateTime.now().startOf('hour').toLocal();
    this.endTime = this.startTime.plus({ hour: 1 });
    this.userId = localStorage.getItem('userId');
  }

  ngOnInit() {
    this.loadAppliances();
  }

  loadAppliances() {
    if (this.userId) {
      this.applianceService.getAppliances(this.userId).subscribe((res) => {
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
    if (this.userId && this.selectedDevice) {
      if (this.userId) {
        this.sharedDataService.setSelectedAppliance(this.selectedDevice);
        this.router.navigateByUrl(
          '/planUsage/'.concat(this.userId, '/', this.selectedDevice.name),
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
