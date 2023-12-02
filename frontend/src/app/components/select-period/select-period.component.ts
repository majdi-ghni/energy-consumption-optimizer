import { Component, Inject, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { ElectricityDataComponent } from '../electricity-data/electricity-data.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DateTime } from 'luxon';

@Component({
  selector: 'app-select-period',
  standalone: true,
  imports: [CommonModule, ElectricityDataComponent],
  templateUrl: './select-period.component.html',
  styleUrls: ['./select-period.component.css'],
})
export class SelectPeriodComponent implements OnInit {
  selectedPeriod: ElectricityPriceAndGreenIndex | null = null;
  actualPriceDate: ElectricityPriceAndGreenIndex | null = null;
  @Output() confirmed: boolean = false;
  startTime!: DateTime;

  constructor(
    private electricityDataService: ElectricityDataService,
    dialogReg: MatDialogRef<SelectPeriodComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  ngOnInit(): void {
    this.selectedPeriod = this.data.selectedPeriod;
    if (this.selectedPeriod) {
      this.startTime = DateTime.fromJSDate(
        new Date(this.selectedPeriod.startTimeStamp) as Date,
      );
    }

    console.log(this.selectedPeriod);
    console.log(this.startTime);
  }

  loadData() {}
}
