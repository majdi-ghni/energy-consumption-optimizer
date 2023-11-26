import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputComponent } from '../input/input.component';
import { SelectMenuComponent } from '../select-menu/select-menu.component';
import { ButtonComponent } from '../button/button.component';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Appliance } from '../../model/applicance/appliance';
import { MatInputModule } from '@angular/material/input';
import { ApplianceUsageType } from '../../model/appliance-usage-type/applianceUsageType';

@Component({
  selector: 'app-modal-box',
  standalone: true,
  imports: [
    CommonModule,
    InputComponent,
    SelectMenuComponent,
    ButtonComponent,
    ReactiveFormsModule,
    MatInputModule,
  ],
  templateUrl: './modal-box.component.html',
  styleUrls: ['./modal-box.component.css'],
})
export class ModalBoxComponent implements OnInit {
  @Input() display: string = 'none';
  addDeviceForm!: FormGroup;
  appliance!: Appliance;
  @Output() onApplianceInitialize = new EventEmitter<any>();
  values = [
    'Geplanter Betrieb (z.B. Waschmaschine)',
    'Dauerbetrieb (24/7, z.B. Kühlschrank)',
    'Sporadischer Betrieb (z.B. Beleuchtung)',
  ];
  usageType!: ApplianceUsageType;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.addDeviceForm = this.formBuilder.group({
      deviceName: ['', Validators.required],
      powerConsumption: ['', Validators.required],
      durationOfUse: ['', Validators.required],
    });
  }

  onAddClick() {
    this.addDeviceForm.markAllAsTouched();
    if (!this.addDeviceForm.valid) {
      console.log('Bitte die Gerät Datenfelder ausfüllen');
      return;
    }

    this.appliance = {
      id: '',
      name: this.addDeviceForm.get('deviceName')?.value,
      powerRating: this.addDeviceForm.get('powerConsumption')?.value, //amount of electrical power the appliance consumes in kilowatts
      estimatedUsageDuration: this.addDeviceForm.get('durationOfUse')?.value, //duration in minutes
      applianceUsageType: this.usageType,
    };
    this.onApplianceInitialize.emit(this.appliance);
    this.display = 'none';
    this.addDeviceForm.reset();
  }

  setUsageType($event: any) {
    if ($event.value == this.values[2]) {
      this.usageType = ApplianceUsageType.SPORADIC_USE;
    } else if ($event.value == this.values[1]) {
      this.usageType = ApplianceUsageType.CONTINUOUS_USE;
    } else this.usageType = ApplianceUsageType.PLANNED_USE;
  }
}