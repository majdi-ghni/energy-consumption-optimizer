import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';

@Component({
  selector: 'app-electricity-data',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './electricity-data.component.html',
  styleUrl: './electricity-data.component.css',
})
export class ElectricityDataComponent implements OnInit {
  electricityData: ElectricityPriceAndGreenIndex | null = null;
  price!: number;
  greenIndex!: number;

  constructor(private electricityDataService: ElectricityDataService) {}

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
          this.price = this.electricityData.price;
          this.greenIndex = this.electricityData.gsi;
        });
    }
  }
}
