import { Component, Inject, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { ElectricityDataComponent } from '../electricity-data/electricity-data.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DateTime } from 'luxon';
import { ButtonComponent } from '../button/button.component';
import { Appliance } from '../../model/applicance/appliance';
import { ApplianceService } from '../../services/apliance/appliance.service';
import { UsagePlanService } from '../../services/usage-plan/usage-plan.service';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { User } from '../../model/user/user';
import { DialogService } from '../../services/dialog/dialog.service';
import { Router } from '@angular/router';
import { UsagePlan } from '../../model/user/usage-plan';

@Component({
  selector: 'app-select-period',
  standalone: true,
  imports: [CommonModule, ElectricityDataComponent, ButtonComponent],
  templateUrl: './select-period.component.html',
  styleUrls: ['./select-period.component.css'],
})
export class SelectPeriodComponent implements OnInit {
  selectedPeriod: ElectricityPriceAndGreenIndex | null = null;
  actualPriceDate: ElectricityPriceAndGreenIndex | null = null;
  selectedDevice: Appliance | null = null;
  @Output() confirmed: boolean = false;
  startTime!: DateTime;
  zipCode: string | null = '';
  message: string = '';
  loggedUser: User | null = null;

  constructor(
    private electricityDataService: ElectricityDataService,
    private dialogRef: MatDialogRef<SelectPeriodComponent>,
    private applianceService: ApplianceService,
    private usagePlanService: UsagePlanService,
    private sharedDateService: SharedDataService,
    private dialogService: DialogService,
    private router: Router,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  ngOnInit(): void {
    this.loadData();
    this.selectedPeriod = this.data.selectedPeriod;
    if (this.selectedPeriod) {
      this.startTime = DateTime.fromJSDate(
        new Date(this.selectedPeriod.startTimeStamp) as Date,
      );
    }
  }

  loadData() {
    this.zipCode = localStorage.getItem('zipCode');
    if (this.zipCode) {
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.message = this.data.message;
          this.selectedDevice = this.data.selectedDevice;
        });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  onConfirm() {
    this.sharedDateService.getLoggedUser().subscribe((res) => {
      this.loggedUser = res;
      if (this.loggedUser && this.selectedDevice && this.selectedPeriod) {
        const usagePlan: UsagePlan = {
          id: '',
          userId: this.loggedUser.id,
          applianceId: this.selectedDevice.id,
          usagePeriodId: this.selectedPeriod.id,
          price: this.selectedPeriod.price,
          gsi: this.selectedPeriod.gsi,
        };
        this.usagePlanService
          .createUsagePlanObject(usagePlan)
          .subscribe((res) => {
            this.dialogRef.close();
            this.router.navigate(['/home']);
          });
      }
    });
  }
}
