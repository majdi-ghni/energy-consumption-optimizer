import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { UsagePlan } from '../../model/user/usage-plan';

const apiUrl = environment.apiUrl.concat('/electricity-usage');

@Injectable({
  providedIn: 'root',
})
export class UsagePlanService {
  constructor(private httpClient: HttpClient) {}

  createUsagePlanObject(usagePlan: UsagePlan) {
    console.log(usagePlan);
    return this.httpClient.post<UsagePlan>(`${apiUrl}/create`, usagePlan);
  }

  getUsagePlaneObject(id: string) {
    return this.httpClient.get<UsagePlan>(`${apiUrl}/${id}`);
  }

  updateUsagePlanObject(usagePlan: UsagePlan) {
    return this.httpClient.post<UsagePlan>(`${apiUrl}/update`, usagePlan);
  }

  getAllUsagesByUserId(userId: string) {
    console.log(`/get/all/by-user/${userId}`);
    return this.httpClient.get<UsagePlan[]>(
      `${apiUrl}/get/all/by-user/${userId}`,
    );
  }

  getAllUsagesByUsagePeriodId(usagePeriodId: string) {
    return this.httpClient.get<UsagePlan[]>(
      `${apiUrl}/get/all/by-usagePeriodId/${usagePeriodId}`,
    );
  }

  getAllUsagesByUsageApplianceId(applianceId: string) {
    return this.httpClient.get<UsagePlan[]>(
      `${apiUrl}/get/all/by-applianceId/${applianceId}`,
    );
  }

  deleteUsagePlanObject(id: string) {
    return this.httpClient.delete<UsagePlan>(`${apiUrl}/delete/${id}`);
  }
}
