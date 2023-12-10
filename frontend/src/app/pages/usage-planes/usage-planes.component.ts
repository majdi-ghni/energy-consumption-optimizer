import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Appliance } from '../../model/applicance/appliance';
import { ApplianceService } from '../../services/apliance/appliance.service';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';
import { BackgroundComponent } from '../../components/background/background.component';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { UsagePlanService } from '../../services/usage-plan/usage-plan.service';
import { UsagePlan } from '../../model/user/usage-plan';

@Component({
  selector: 'app-usage-planes',
  standalone: true,
  imports: [
    CommonModule,
    ModalBoxComponent,
    BackgroundComponent,
    SideNavComponent,
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

  constructor(
    private applianceService: ApplianceService,
    private sharedDataService: SharedDataService,
    private activatedRoute: ActivatedRoute,
    private usagePlanService: UsagePlanService,
    private router: Router,
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

  addAppliance(event: any) {
    this.applianceService.addAppliance(event, this.userId).subscribe((res) => {
      this.appliances.push(res);
      console.log(res);
    });
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
    console.log(usagePlan);
  }

  onEditIconClick(usagePlan: UsagePlan) {
    console.log(usagePlan);
  }

  navigateToDashboard() {
    this.router.navigate(['/home']);
  }
}
