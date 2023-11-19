import { Address } from '../address/address';
import { Role } from '../role/role';
import { Appliance } from '../applicance/appliance';
import { ElectricityPriceAndGreenIndex } from '../electricity-price-and-green-index/electricityPriceAndGreenIndex';

export interface User {
  id: string;
  username: string;
  address: Address;
  password: string;
  email: string;
  role: Role;
  actualTariff: number;
  savedCo2Footprint: number;
  totalCo2Footprint: number;
  totalElectricityCost: number;
  lastMonthBill: number;
  appliances: Appliance[];
  electricityPriceAndGreenIndexList: ElectricityPriceAndGreenIndex[];
}
