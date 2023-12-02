import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';
import { DateTime } from 'luxon';

@Component({
  selector: 'app-electricity-data',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './electricity-data.component.html',
  styleUrls: ['./electricity-data.component.css'],
})
export class ElectricityDataComponent implements OnInit {
  electricityData: ElectricityPriceAndGreenIndex | null = null;
  @Input() price!: number;
  @Input() greenIndex!: number;
  @Input() startTime!: DateTime;
  endTime!: DateTime;
  city: string = '';

  constructor(private electricityDataService: ElectricityDataService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.loadStartAndEndDate();
    const zipCode = localStorage.getItem('zipCode');
    if (zipCode != null) {
      this.electricityDataService
        .getActualElectricityData(zipCode)
        .subscribe((res) => {
          this.electricityData = res;
          this.city = this.electricityData.city;
          if (!(this.price && this.greenIndex)) {
            this.price = this.electricityData.price / 1000;
            this.greenIndex = this.electricityData.gsi;
          }
        });
    }
  }

  private loadStartAndEndDate() {
    if (!this.startTime) {
      this.startTime = DateTime.now().startOf('hour').toLocal();
    }
    this.endTime = this.startTime.plus({ hour: 1 });
  }
}
