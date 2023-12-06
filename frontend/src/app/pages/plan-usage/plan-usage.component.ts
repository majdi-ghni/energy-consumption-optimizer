import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackgroundComponent } from '../../components/background/background.component';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { ElectricityDataComponent } from '../../components/electricity-data/electricity-data.component';
import { RouterLink } from '@angular/router';
import { SelectMenuComponent } from '../../components/select-menu/select-menu.component';
import { ElectricityDataService } from '../../services/electricity-data/electricity-data.service';
import { User } from '../../model/user/user';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';
import { SelectMenu } from '../../model/select-menu';
import { DialogService } from '../../services/dialog/dialog.service';
import { SelectPeriodComponent } from '../../components/select-period/select-period.component';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { Appliance } from '../../model/applicance/appliance';

@Component({
  selector: 'app-plan-usage',
  standalone: true,
  imports: [
    CommonModule,
    BackgroundComponent,
    SideNavComponent,
    ElectricityDataComponent,
    RouterLink,
    SelectMenuComponent,
  ],
  templateUrl: './plan-usage.component.html',
  styleUrls: ['./plan-usage.component.css'],
})
export class PlanUsageComponent implements OnInit {
  @Input() loggedUser: User | null = null;
  selectedPeriod: ElectricityPriceAndGreenIndex | null = null;
  values: SelectMenu[] = [];
  electricityData: ElectricityPriceAndGreenIndex[] = [];
  zipCode: string | null = null;
  placeHolder = 'Wählen Sie Startzeitpunkt';
  cheapestHour: ElectricityPriceAndGreenIndex | null = null;
  greenHour: ElectricityPriceAndGreenIndex | null = null;
  expensiveHour: ElectricityPriceAndGreenIndex | null = null;
  highEmissionsHour: ElectricityPriceAndGreenIndex | null = null;
  actualElectricityData: ElectricityPriceAndGreenIndex | null = null;
  private selectedDevice: Appliance | null = null;

  constructor(
    private electricityDataService: ElectricityDataService,
    private dialogService: DialogService,
    private sharedDataService: SharedDataService,
  ) {}

  ngOnInit() {
    this.loadData();
  }

  private loadData() {
    this.zipCode = localStorage.getItem('zipCode');
    this.sharedDataService.getSelectedAppliance().subscribe((res) => {
      this.selectedDevice = res;
      this.getSuggestedPeriods();
      this.getElectricityForecastData();
    });
  }

  formatElectricityData(data: ElectricityPriceAndGreenIndex) {
    // Build the formatted string
    const formattedString = `${data.startTimeStamp.toLocaleString('de-DE', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    })} - ${data.endTimeStamp.toLocaleString('de-DE', {
      hour: 'numeric',
      minute: 'numeric',
    })}, ${data.price.toFixed(2)} ${data.priceUnit}, ${
      data.standardElectricityCo2InGram
    }g je kWh`;
    this.values.push({ displayedValue: formattedString, objectValue: data });
  }

  onSelect($event: any) {
    if (this.zipCode)
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.actualElectricityData = res;
          this.selectedPeriod = $event.objectValue;
          if (this.selectedPeriod) {
            const emissionSave = Math.abs(
              this.selectedPeriod?.gsi - this.actualElectricityData.gsi,
            );
            const selectedPrice = (this.selectedPeriod?.price / 1000).toFixed(
              2,
            );
            const actualPrice = (
              this.actualElectricityData?.price / 1000
            ).toFixed(2);
            const dialogRef = this.dialogService.openDialog(
              SelectPeriodComponent,
              {
                data: {
                  selectedPeriod: this.selectedPeriod,
                  title: 'Nutzungszeitraum ausgewählt!',
                  message: `Ihrer Auswahl verursachen sie <strong>${this.greenHour?.gsi} g</strong> CO₂ je Kilo-Watt-Stunde anstelle <strong>${this.actualElectricityData.gsi} g</strong>. Sie kostet Ihnen <strong>${selectedPrice} cent</strong> anstelle von <strong>${actualPrice} cent</strong> pro kWh.`,
                  selectedDevice: this.selectedDevice,
                },
              },
            );
            dialogRef.subscribe((result) => console.log('result: ', result));
          }
        });
  }

  getElectricityForecastData() {
    if (this.zipCode) {
      this.electricityDataService
        .getElectricityDataForecast(this.zipCode)
        .subscribe((res) => {
          this.electricityData = res;
          this.electricityData.forEach((data) =>
            this.formatElectricityData(data),
          );
          if (!(this.values.length > 0)) {
            this.values.push({ displayedValue: '', objectValue: null });
          }
        });
    }
  }

  getSuggestedPeriods() {
    this.electricityDataService
      .getCheapestHour(this.zipCode)
      ?.subscribe((res) => (this.cheapestHour = res));
    this.electricityDataService.getGreenHour(this.zipCode)?.subscribe((res) => {
      this.greenHour = res;
    });
    this.electricityDataService
      .getExpensiveHour(this.zipCode)
      ?.subscribe((res) => (this.expensiveHour = res));
    this.electricityDataService
      .getHighEmissionsHour(this.zipCode)
      ?.subscribe((res) => (this.highEmissionsHour = res));
  }

  selectGreen() {
    if (this.zipCode)
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.actualElectricityData = res;
          if (this.greenHour) {
            const emissionSave = Math.abs(
              this.greenHour?.gsi - this.actualElectricityData.gsi,
            );
            const selectedPrice = (this.greenHour?.price / 1000).toFixed(2);
            const actualPrice = (
              this.actualElectricityData?.price / 1000
            ).toFixed(2);
            const dialogRef = this.dialogService.openDialog(
              SelectPeriodComponent,
              {
                data: {
                  selectedPeriod: this.greenHour,
                  title: 'Sie haben grün gewählt!',
                  message: `Ihre Stromauswahl ist umweltfreundlich. Mit Ihrer Auswahl verursachen sie <strong>${this.greenHour?.gsi} g </strong> CO₂ je Kilo-Watt-Stunde anstelle <strong>${this.actualElectricityData.gsi} g</strong>. Sie kostet Ihnen <strong>${selectedPrice} cent</strong> anstelle von <strong>${actualPrice} cent</strong> pro kWh.`,
                  selectedDevice: this.selectedDevice,
                },
              },
            );
            dialogRef.subscribe((result) => console.log('result: ', result));
          }
        });
  }

  selectCheap() {
    if (this.zipCode)
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.actualElectricityData = res;
          if (this.cheapestHour) {
            const emissions = this.cheapestHour?.gsi;
            const price = (this.cheapestHour?.price / 1000).toFixed(2);
            const dialogRef = this.dialogService.openDialog(
              SelectPeriodComponent,
              {
                data: {
                  selectedPeriod: this.cheapestHour,
                  title: 'Kosteneffiziente Wahl',
                  message: `Ihre Stromauswahl ist günstig und spart Ihnen <strong>${price} cent</strong> je Kilo-Watt-Stunde im Vergleich zu aktuellem Strompreis. Ihr Verbrauch verursacht <strong>${emissions} g </strong> CO₂ je Kilo-Watt-Stunde anstelle <strong>${this.actualElectricityData.gsi} g</strong>.`,
                  selectedDevice: this.selectedDevice,
                },
              },
            );
            dialogRef.subscribe((result) => console.log('result: ', result));
          }
        });
  }

  selectHighEmissions() {
    if (this.zipCode)
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.actualElectricityData = res;
          if (this.highEmissionsHour) {
            const emissions = this.highEmissionsHour?.gsi;
            const selectedPrice = (
              this.highEmissionsHour?.price / 1000
            ).toFixed(2);
            const actualPrice = (
              this.actualElectricityData?.price / 1000
            ).toFixed(2);
            const dialogRef = this.dialogService.openDialog(
              SelectPeriodComponent,
              {
                data: {
                  selectedPeriod: this.highEmissionsHour,
                  title: 'Achtung, hohe Emissionen!',
                  message: `Ihre Stromauswahl kostet <strong>${selectedPrice} cent</strong> je Kilo-Watt-Stunde anstelle von <strong>${actualPrice} cent</strong>. Ihr Verbrauch verursacht <strong>${emissions} g </strong> CO₂ je Kilo-Watt-Stunde anstelle <strong>${this.actualElectricityData.gsi} g</strong>.`,
                  selectedDevice: this.selectedDevice,
                },
              },
            );
            dialogRef.subscribe((result) => console.log('result: ', result));
          }
        });
  }

  selectExpensive() {
    if (this.zipCode)
      this.electricityDataService
        .getActualElectricityData(this.zipCode)
        .subscribe((res) => {
          this.actualElectricityData = res;
          if (this.expensiveHour) {
            const emissions = this.expensiveHour?.gsi;
            const selectedPrice = (this.expensiveHour?.price / 1000).toFixed(2);
            const actualPrice = (
              this.actualElectricityData?.price / 1000
            ).toFixed(2);
            const dialogRef = this.dialogService.openDialog(
              SelectPeriodComponent,
              {
                data: {
                  selectedPeriod: this.highEmissionsHour,
                  title: 'Achtung, Spitzenpreis!',
                  message: `Ihre Stromauswahl kostet <strong>${selectedPrice} cent</strong> je Kilo-Watt-Stunde anstelle von <strong>${actualPrice} cent</strong>. Ihr Verbrauch verursacht <strong>${emissions} g </strong> CO₂ je Kilo-Watt-Stunde anstelle <strong>${this.actualElectricityData.gsi} g</strong>.`,
                  selectedDevice: this.selectedDevice,
                },
              },
            );
            dialogRef.subscribe((result) => console.log('result: ', result));
          }
        });
  }
}
