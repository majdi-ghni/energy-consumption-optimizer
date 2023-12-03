import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';

const apiUrl = environment.apiUrl.concat('/electricity-price-and-green-index');

@Injectable({
  providedIn: 'root',
})
export class ElectricityDataService {
  constructor(private httpClient: HttpClient) {}

  getActualElectricityData(zipCode: string) {
    return this.httpClient.get<ElectricityPriceAndGreenIndex>(
      `${apiUrl}/actual?zipCode=${zipCode}`,
    );
  }

  getElectricityDataForecast(zipCode: string) {
    return this.httpClient.get<ElectricityPriceAndGreenIndex[]>(
      `${apiUrl}/forecast/${zipCode}`,
    );
  }

  getCheapestHour(zipCode: string | null) {
    if (zipCode)
      return this.httpClient.get<ElectricityPriceAndGreenIndex>(
        `${apiUrl}/cheapest-hour/${zipCode}`,
      );
    return null;
  }

  getGreenHour(zipCode: string | null) {
    if (zipCode)
      return this.httpClient.get<ElectricityPriceAndGreenIndex>(
        `${apiUrl}/green-hour/${zipCode}`,
      );
    return null;
  }

  getExpensiveHour(zipCode: string | null) {
    if (zipCode)
      return this.httpClient.get<ElectricityPriceAndGreenIndex>(
        `${apiUrl}/expensive-hour/${zipCode}`,
      );
    return null;
  }

  getHighEmissionsHour(zipCode: string | null) {
    if (zipCode)
      return this.httpClient.get<ElectricityPriceAndGreenIndex>(
        `${apiUrl}/highest-emissions-hour/${zipCode}`,
      );
    return null;
  }
}
