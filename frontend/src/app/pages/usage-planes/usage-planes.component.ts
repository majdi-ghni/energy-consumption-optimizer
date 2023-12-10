import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Appliance } from '../../model/applicance/appliance';
import { ApplianceService } from '../../services/apliance/appliance.service';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { ActivatedRoute } from '@angular/router';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';
import { BackgroundComponent } from '../../components/background/background.component';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { UsagePlanService } from '../../services/usage-plan/usage-plan.service';
import { UsagePlan } from '../../model/user/usage-plan';
import { NavComponent } from '../../components/nav/nav.component';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';
import { DialogService } from '../../services/dialog/dialog.service';
import { PeriodDataComponent } from '../../components/period-data/period-data.component';

@Component({
  selector: 'app-usage-planes',
  standalone: true,
  imports: [
    CommonModule,
    ModalBoxComponent,
    BackgroundComponent,
    SideNavComponent,
    NavComponent,
  ],
  templateUrl: './usage-planes.component.html',
  styleUrls: ['./usage-planes.component.css'],
})
export class UsagePlanesComponent implements OnInit {
  searchValue: string = '';
  appliances: Appliance[] = [];
  filteredAppliances: Appliance[] = [];
  userId: string = '';
  searchInput: string = '';
  usagePlans: UsagePlan[] = [];
  filteredUsagePlans: UsagePlan[] = [];
  usagePeriodDataArray: ElectricityPriceAndGreenIndex[] = [];

  constructor(
    private applianceService: ApplianceService,
    private sharedDataService: SharedDataService,
    private activatedRoute: ActivatedRoute,
    private usagePlanService: UsagePlanService,
    private electricityDataService: ElectricityDataService,
    private dialogService: DialogService,
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.userId = this.activatedRoute.snapshot.paramMap.get('userId') as string;
    this.getUsagePlans();
  }

  getUsagePlans() {
    this.usagePlanService.getAllUsagesByUserId(this.userId).subscribe((res) => {
      this.usagePlans = res;
      this.filteredUsagePlans = this.usagePlans;
      this.getAppliances();
    });
  }

  getAppliances() {
    this.applianceService.getAppliances(this.userId).subscribe((res) => {
      this.appliances = res;
      this.filteredAppliances = this.appliances;
    });
  }

  onSearchInputChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input) {
      this.searchValue = input.value;
      this.filteredUsagePlans = this.usagePlans.filter((plan) =>
        plan.deviceName.toLowerCase().includes(this.searchValue.toLowerCase()),
      );
    }
  }

  deleteAppliance(usagePlan: UsagePlan) {
    this.usagePlanService.deleteUsagePlanObject(usagePlan.id).subscribe(() => {
      const index = this.usagePlans.indexOf(usagePlan);
      const indexOfFilteredArray = this.filteredUsagePlans.indexOf(usagePlan);
      if (index != -1) {
        this.usagePlans.splice(index, 1);
      }
      if (indexOfFilteredArray != -1) {
        this.filteredUsagePlans.splice(indexOfFilteredArray, 1);
      }
    });
  }

  onInfoIconClick(usagePlan: UsagePlan) {
    this.electricityDataService
      .getActualElectricityDataById(usagePlan.usagePeriodId)
      .subscribe((res) => {
        const data = res;
        this.dialogService.openDialog(PeriodDataComponent, {
          data,
        });
      });
  }

  onEditIconClick(usagePlan: UsagePlan) {
    console.log(usagePlan);
  }
}
