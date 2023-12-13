import { Component, Inject, OnInit } from '@angular/core';
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
import { ApplianceService } from '../../services/apliance/appliance.service';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { UserService } from '../../services/user/user.service';
import { User } from '../../model/user/user';

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
    MatDialogModule,
  ],
  templateUrl: './modal-box.component.html',
  styleUrls: ['./modal-box.component.css'],
})
export class ModalBoxComponent implements OnInit {
  display: string = 'none';
  editDevice: Appliance | null = null;

  addDeviceForm!: FormGroup;
  appliance!: Appliance;
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
  userId: string | null = null;
  user: User | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<any>,
    private applianceService: ApplianceService,
    private userService: UserService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.editDevice = this.data;
    if (this.editDevice && this.editDevice.user) {
      this.userId = this.editDevice.user.id;
    }
  }

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
      this.setUsageType(this.editDevice.applianceUsageType);
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

    if (this.userId) {
      this.userService.getUser(this.userId).subscribe((res) => {
        this.user = res;
        this.updateAppliance();
      });
    } else {
      this.userId = localStorage.getItem('userId');
      if (this.userId) {
        this.userService.getUser(this.userId).subscribe((res) => {
          this.user = res;
          this.createAppliance();
        });
      }
      // user not yet registered
      else {
        this.addApplianceBeforeRegister();
      }
    }
  }

  setUsageType($event: any) {
    if ($event.displayedValue == this.values[2].displayedValue) {
      this.usageType = ApplianceUsageType.SPORADIC_USE;
      this.addDeviceForm.controls['durationOfUse'].enable(); // Enable the control
      this.addDeviceForm.reset({ durationOfUse: '' });
    } else if ($event.displayedValue == this.values[0].displayedValue) {
      this.usageType = ApplianceUsageType.PLANNED_USE;
      this.addDeviceForm.controls['durationOfUse'].enable(); // Enable the control
      this.addDeviceForm.reset({ durationOfUse: '' });
    } else {
      this.usageType = ApplianceUsageType.CONTINUOUS_USE;
      this.addDeviceForm.controls['durationOfUse'].setValue(24 * 60); // setting 24/7 usage duration
      this.addDeviceForm.controls['durationOfUse'].disable();
    }
  }

  onCancelClick() {
    this.display = 'none';
    this.addDeviceForm.reset();
    this.dialogRef.close();
  }

  updateAppliance() {
    if (this.editDevice && this.user) {
      console.log('edit mode');
      this.appliance = {
        id: this.editDevice.id,
        name: this.addDeviceForm.get('deviceName')?.value,
        powerRating: this.addDeviceForm.get('powerConsumption')?.value, //amount of electrical power the appliance consumes in kilowatts
        estimatedUsageDuration: this.addDeviceForm.get('durationOfUse')?.value, //duration in minutes
        applianceUsageType: this.usageType,
        user: this.user,
      };
      this.applianceService.updateAppliance(this.appliance).subscribe((res) => {
        this.addDeviceForm.reset();
        this.dialogRef.close({ res });
      });
    }
  }

  createAppliance() {
    if (this.user) {
      this.appliance = {
        id: '',
        name: this.addDeviceForm.get('deviceName')?.value,
        powerRating: this.addDeviceForm.get('powerConsumption')?.value, //amount of electrical power the appliance consumes in kilowatts
        estimatedUsageDuration: this.addDeviceForm.get('durationOfUse')?.value, //duration in minutes
        applianceUsageType: this.usageType,
        user: this.user,
      };
      console.log('create mode');
      this.applianceService
        .addAppliance(this.appliance, this.user?.id)
        .subscribe((res) => {
          this.addDeviceForm.reset();
          this.dialogRef.close({ res });
        });
    }
  }

  addApplianceBeforeRegister() {
    this.appliance = {
      id: '',
      name: this.addDeviceForm.get('deviceName')?.value,
      powerRating: this.addDeviceForm.get('powerConsumption')?.value, //amount of electrical power the appliance consumes in kilowatts
      estimatedUsageDuration: this.addDeviceForm.get('durationOfUse')?.value, //duration in minutes
      applianceUsageType: this.usageType,
    };
    const appliance = this.appliance;
    this.addDeviceForm.reset();
    this.dialogRef.close({ appliance });
  }
}
