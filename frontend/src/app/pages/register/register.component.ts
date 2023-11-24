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
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private tokenService: SessionManagementService,
  ) {}

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
    let user: User = {
      id: '',
      username: this.registerForm.get('username')?.value,
      email: this.registerForm.get('email')?.value,
      password: this.registerForm.get('password')?.value,
      address: {
        city: this.registerForm.get('city')?.value,
        zipCode: this.registerForm.get('zipCode')?.value,
      },
      actualTariff: this.registerForm.get('actualTariff')?.value,
      role: Role.ROLE_USER,
      savedCo2Footprint: 0,
      totalCo2Footprint: 0,
      totalElectricityCost: 0,
      lastMonthBill: 0,
      appliances: [],
    };
    console.log(user);
    this.userService.createUser(user).subscribe((res) => {
      if (typeof res == 'number') {
        console.log('Fehler beim Registrieren');
      }
      this.authService.login(user.username, user.password).subscribe((data) => {
        this.tokenService.saveUser(data);
        localStorage.setItem('access_token', data.tokens.BEARER);
        this.router.navigate(['/home']);
      });
    });
  }
}
