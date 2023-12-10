import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackgroundComponent } from '../../components/background/background.component';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { ButtonComponent } from '../../components/button/button.component';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';
import { ApplianceService } from '../../services/apliance/appliance.service';
import { SharedDataService } from '../../services/shared-data/shared-data.service';
import { Appliance } from '../../model/applicance/appliance';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-appliances',
  standalone: true,
  imports: [
    CommonModule,
    BackgroundComponent,
    SideNavComponent,
    ButtonComponent,
    ModalBoxComponent,
  ],
  templateUrl: './appliances.component.html',
  styleUrls: ['./appliances.component.css'],
})
export class AppliancesComponent implements OnInit {
  searchValue: string = '';
  display: string = 'none ';
  isModalOpen: boolean = false;
  appliances: Appliance[] = [];
  filteredAppliances: Appliance[] = [];
  userId: string = '';
  searchInput: string = '';

  constructor(
    private applianceService: ApplianceService,
    private sharedDataService: SharedDataService,
    private activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.userId = this.activatedRoute.snapshot.paramMap.get('userId') as string;
    this.getAppliances();
  }

  getAppliances() {
    this.applianceService.getAppliances(this.userId).subscribe((res) => {
      this.appliances = res;
      this.filteredAppliances = this.appliances;
    });
  }

  onSearchInputChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input) {
      this.searchValue = input.value;
      this.filteredAppliances = this.appliances.filter((device) =>
        device.name.toLowerCase().includes(this.searchValue.toLowerCase()),
      );
    }
  }

  addAppliance(event: any) {
    this.applianceService.addAppliance(event, this.userId).subscribe((res) => {
      this.appliances.push(res);
      console.log(res);
    });
  }

  openModalClicked() {
    this.isModalOpen = !this.isModalOpen;
    if (this.isModalOpen) {
      this.display = 'block';
    } else {
      this.display = 'none';
    }
  }

  deleteAppliance(device: Appliance) {
    this.applianceService.deleteAppliance(device).subscribe(() => {
      const index = this.appliances.indexOf(device);
      const filteredAppliancesIndex = this.filteredAppliances.indexOf(device);
      if (index !== -1) {
        this.appliances.splice(index, 1);
      }
      if (filteredAppliancesIndex != -1) {
        this.filteredAppliances.splice(filteredAppliancesIndex, 1);
      }
    });
  }

  onInfoIconClick(device: Appliance) {
    console.log(device);
    //TODO: page to display info
  }

  onEditIconClick(device: Appliance) {
    console.log(device);
    //ToDO: formGroup as input to modal box component
    this.applianceService.deleteAppliance(device);
  }
}
