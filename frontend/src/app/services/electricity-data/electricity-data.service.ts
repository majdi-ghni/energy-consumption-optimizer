import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';

const apiUrl = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class ElectricityDataService {
  constructor(private httpClient: HttpClient) {}

  getActualElectricityData(zipCode: string) {
    return this.httpClient.get<ElectricityPriceAndGreenIndex>(
      `${apiUrl}/electricity-price-and-green-index/actual?zipCode=${zipCode}`,
    );
  }
}
