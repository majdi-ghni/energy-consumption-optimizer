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
import { SelectMenu } from '../../model/select-menu';

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
  @Input() editDevice: Appliance | null = null;

  addDeviceForm!: FormGroup;
  appliance!: Appliance;
  @Output() onApplianceInitialize = new EventEmitter<any>();
  values: SelectMenu[] = [
    {
      displayedValue: 'Geplanter Betrieb (z.B. Waschmaschine)',
      objectValue: null,
    },
    {
      displayedValue: 'Dauerbetrieb (24/7, z.B. Kühlschrank)',
      objectValue: null,
    },
    {
      displayedValue: 'Sporadischer Betrieb (z.B. Beleuchtung)',
      objectValue: null,
    },
  ];
  usageType: ApplianceUsageType = ApplianceUsageType.CONTINUOUS_USE;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    // Edit device mode
    if (this.editDevice) {
      this.addDeviceForm = this.formBuilder.group({
        deviceName: [this.editDevice.name, Validators.required],
        powerConsumption: [this.editDevice.powerRating, Validators.required],
        durationOfUse: [
          this.editDevice.estimatedUsageDuration,
          Validators.required,
        ],
      });
    }
    // add new device mode
    else {
      this.addDeviceForm = this.formBuilder.group({
        deviceName: ['', Validators.required],
        powerConsumption: ['', Validators.required],
        durationOfUse: ['', Validators.required],
      });
    }
  }

  onAddClick() {
    this.addDeviceForm.markAllAsTouched();
    if (
      !(this.usageType == ApplianceUsageType.CONTINUOUS_USE) &&
      this.addDeviceForm.invalid
    ) {
      console.log('Bitte die Gerät Datenfelder ausfüllen');
      return;
    } else if (
      this.usageType == ApplianceUsageType.CONTINUOUS_USE &&
      (this.addDeviceForm.get('deviceName')?.invalid ||
        this.addDeviceForm.get('powerConsumption')?.invalid)
    ) {
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
    this.usageType = ApplianceUsageType.CONTINUOUS_USE;
  }

  setUsageType($event: any) {
    console.log($event);
    if ($event.displayedValue == this.values[2].displayedValue) {
      console.log(ApplianceUsageType.SPORADIC_USE);
      this.usageType = ApplianceUsageType.SPORADIC_USE;
      this.addDeviceForm.controls['durationOfUse'].enable(); // Enable the control
      this.addDeviceForm.reset({ durationOfUse: '' });
    } else if ($event.displayedValue == this.values[0].displayedValue) {
      console.log(ApplianceUsageType.PLANNED_USE);
      this.usageType = ApplianceUsageType.PLANNED_USE;
      this.addDeviceForm.controls['durationOfUse'].enable(); // Enable the control
      this.addDeviceForm.reset({ durationOfUse: '' });
    } else {
      console.log(ApplianceUsageType.CONTINUOUS_USE);
      this.usageType = ApplianceUsageType.CONTINUOUS_USE;
      this.addDeviceForm.controls['durationOfUse'].setValue(24 * 60); // setting 24/7 usage duration
      this.addDeviceForm.controls['durationOfUse'].disable();
    }
  }

  onCancelClick() {
    this.display = 'none';
    this.addDeviceForm.reset();
  }

  getUsageType() {
    return (
      this.usageType.startsWith('SPORADIC_USE') ||
      this.usageType.startsWith('PLANNED_USE')
    );
  }
}
