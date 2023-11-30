import { Component, OnInit } from '@angular/core';
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
  price!: number;
  greenIndex!: number;
  city: string = '';
  startTime: DateTime;
  endTime: DateTime;

  constructor(private electricityDataService: ElectricityDataService) {
    this.startTime = DateTime.now().startOf('hour').toLocal();
    this.endTime = this.startTime.plus({ hour: 1 });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    const zipCode = localStorage.getItem('zipCode');
    if (zipCode != null) {
      this.electricityDataService
        .getActualElectricityData(zipCode)
        .subscribe((res) => {
          this.electricityData = res;
          this.price = this.electricityData.price / 1000;
          this.greenIndex = this.electricityData.gsi;
          this.city = this.electricityData.city;
        });
    }
  }
}
