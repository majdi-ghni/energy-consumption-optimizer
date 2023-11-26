import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../components/button/button.component';
import { BackgroundComponent } from '../../components/background/background.component';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../services/user/user.service';
import { User } from '../../model/user/user';
import { Role } from '../../model/role/role';
import { AuthService } from '../../services/auth/auth.service';
import { SessionManagementService } from '../../services/auth/session-management.service';
import { ModalBoxComponent } from '../../components/modal-box/modal-box.component';
import { InputComponent } from '../../components/input/input.component';
import { SelectMenuComponent } from '../../components/select-menu/select-menu.component';
import { Appliance } from '../../model/applicance/appliance';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    FormsModule,
    ButtonComponent,
    BackgroundComponent,
    RouterLink,
    ModalBoxComponent,
    InputComponent,
    SelectMenuComponent,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class RegisterComponent implements OnInit {
  user: User;
  registerForm!: FormGroup;
  isModalOpen: boolean = false;
  values = [
    'Geplanter Betrieb (z.B. Waschmaschine)',
    'Dauerbetrieb (24/7, z.B. Kühlschrank)',
    'Sporadischer Betrieb (z.B. Beleuchtung)',
  ];
  display: string = 'none ';
  appliances: Appliance[] = [];
  deleteDevice: Appliance | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private tokenService: SessionManagementService,
  ) {
    this.user = {
      id: '',
      username: '',
      email: '',
      password: '',
      address: {
        city: '',
        zipCode: '',
      },
      actualTariff: 0,
      role: Role.ROLE_USER,
      savedCo2Footprint: 0,
      totalCo2Footprint: 0,
      totalElectricityCost: 0,
      lastMonthBill: 0,
      appliances: [],
    };
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', Validators.required, Validators.email],
      password: ['', Validators.required],
      city: ['', Validators.required],
      zipCode: ['', Validators.required],
      actualTariff: ['', Validators.required],
    });
  }

  onRegisterClick() {
    this.registerForm.markAllAsTouched();
    if (!this.registerForm.valid) {
      console.log('Datenfelder ausfüllen');
    }
    this.user.id = '';
    this.user.username = this.registerForm.get('username')?.value;
    this.user.email = this.registerForm.get('email')?.value;
    this.user.password = this.registerForm.get('password')?.value;
    this.user.address = {
      city: this.registerForm.get('city')?.value,
      zipCode: this.registerForm.get('zipCode')?.value,
    };
    this.user.actualTariff = this.registerForm.get('actualTariff')?.value;
    this.user.role = Role.ROLE_USER;
    this.user.savedCo2Footprint = 0;
    this.user.totalCo2Footprint = 0;
    this.user.totalElectricityCost = 0;
    this.user.lastMonthBill = 0;
    console.log(this.appliances);
    console.log(this.user);
    //TODO: create appliance service. first save user, then get its id, then save appliances from frontend through calling its service in BE and giving the saved User id
    this.userService.createUser(this.user).subscribe((res) => {
      if (typeof res == 'number') {
        console.log('Fehler beim Registrieren');
      }
      this.authService
        .login(this.user.username, this.user.password)
        .subscribe((data) => {
          this.tokenService.saveUser(data);
          localStorage.setItem('access_token', data.tokens.BEARER);
          this.router.navigate(['/home']);
        });
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

  addAppliance($event: any) {
    this.appliances.push($event);
    this.user.appliances = this.appliances;
    console.log(this.appliances);
    console.log(this.user);
  }

  deleteAppliance(device: Appliance) {
    const index = this.appliances.indexOf(device);
    if (index !== -1) {
      this.appliances.splice(index, 1);
    }
  }
}
