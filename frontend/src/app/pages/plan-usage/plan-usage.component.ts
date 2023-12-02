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

  constructor(
    private electricityDataService: ElectricityDataService,
    private dialogService: DialogService,
  ) {}

  ngOnInit() {
    this.loadData();
  }

  private loadData() {
    this.zipCode = localStorage.getItem('zipCode');
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
    } else {
      console.log(
        'Postleitzahl nicht Gefunden! Versuchen es bitte später erneut',
      );
    }
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
    console.log($event.objectValue);
    this.selectedPeriod = $event.objectValue;
    const dialogRef = this.dialogService.openDialog(SelectPeriodComponent, {
      data: { selectedPeriod: this.selectedPeriod, title: '' },
    });
    dialogRef.subscribe((result) => console.log('result: ', result));
  }
}
