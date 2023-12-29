export interface ElectricityPriceAndGreenIndex {
  id: string;
  startTimeStamp: Date;
  endTimeStamp: Date;
  price: number;
  priceUnit: string;
  gsi: number;
  standardElectricityCo2InGram: number; // holds the weight of co2 of standard electricity in gram unit
  city: string;
  zipCode: string;
}
