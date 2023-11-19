import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideNavComponent } from '../../components/side-nav/side-nav.component';
import { BackgroundComponent } from '../../components/background/background.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, SideNavComponent, BackgroundComponent],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css', '../register/register.component.css'],
})
export class HomepageComponent {}
