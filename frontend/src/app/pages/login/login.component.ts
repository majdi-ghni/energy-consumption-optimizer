import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../components/button/button.component';
import { MatInputModule } from '@angular/material/input';
import { BackgroundComponent } from '../../components/background/background.component';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { SessionManagementService } from '../../services/auth/session-management.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
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
    HttpClientModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css', '../register/register.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup = this.formBuilder.group({
    userNameOrEmail: ['', Validators.required],
    password: ['', Validators.required],
  });
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private httpClient: HttpClient,
    private tokenService: SessionManagementService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    console.log('hello log in');
  }

  onLoginClick() {
    const formData = this.loginForm.getRawValue();
    this.authService
      .login(formData.userNameOrEmail, formData.password)
      .subscribe((data: any) => {
        this.tokenService.saveUser(data);
        localStorage.setItem('access_token', data.tokens.access_token);
        this.router.navigate(['/home']);
      });
  }
}
