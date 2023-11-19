import { ApplianceUsageType } from '../appliance-usage-type/applianceUsageType';

export interface Appliance {
  id: string;
  name: string;
  model: string;
  estimatedUsageDuration: number; //duration in minutes
  powerRating: number; //amount of electrical power the appliance consumes in kilowatts
  applianceUsageType: ApplianceUsageType;
}
