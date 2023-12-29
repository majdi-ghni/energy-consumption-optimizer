import { ApplianceUsageType } from '../appliance-usage-type/applianceUsageType';
import { User } from '../user/user';

export interface Appliance {
  id: string;
  name: string;
  estimatedUsageDuration: number; //duration in minutes
  powerRating: number; //amount of electrical power the appliance consumes in kilowatts
  applianceUsageType: ApplianceUsageType;
  user?: User;
}
