import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackgroundComponent } from '../../components/background/background.component';
import { MatInputModule } from '@angular/material/input';
import { ButtonComponent } from '../../components/button/button.component';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    FormsModule,
    ButtonComponent,
    BackgroundComponent,
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: [
    './forgot-password.component.css',
    '../register/register.component.css',
  ],
})
export class ForgotPasswordComponent implements OnInit {
  registerForm!: FormGroup;
  submitted = false;
  infoText =
    'Geben Sie Ihre Email ein, damit wir Ihnen einen Link zum Zurücksetzen des Passworts schicken können.';

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      email: ['', Validators.required],
    });
  }

  onSendClick() {}
}
