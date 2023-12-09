import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Appliance } from '../../model/applicance/appliance';

const apiUrl = environment.apiUrl.concat('/appliance');

@Injectable({
  providedIn: 'root',
})
export class ApplianceService {
  constructor(private httpClient: HttpClient) {}

  addAppliance(appliance: Appliance, userId: string) {
    return this.httpClient.post<Appliance>(
      `${apiUrl}/add/${userId}`,
      appliance,
    );
  }

  getAppliances(userId: string) {
    return this.httpClient.get<Appliance[]>(`${apiUrl}/get/${userId}`);
  }

  getApplianceByUserIdAndApplianceName(userId: string, deviceName: string) {
    return this.httpClient.get<Appliance>(
      `${apiUrl}/get/${userId}/${deviceName}`,
    );
  }

  deleteAppliance(deleteAppliance: Appliance) {
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      body: deleteAppliance,
    };
    return this.httpClient.delete<Appliance>(`${apiUrl}/delete`, options);
  }
}
