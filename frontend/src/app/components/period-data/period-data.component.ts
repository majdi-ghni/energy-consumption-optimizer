import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogService } from '../../services/dialog/dialog.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogRef } from '@angular/cdk/dialog';
import { ButtonComponent } from '../button/button.component';
import { ElectricityPriceAndGreenIndex } from '../../model/electricity-price-and-green-index/electricityPriceAndGreenIndex';

@Component({
  selector: 'app-period-data',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './period-data.component.html',
  styleUrls: ['./period-data.component.css'],
})
export class PeriodDataComponent {
  constructor(
    private dialogService: DialogService,
    private dialogRef: DialogRef<PeriodDataComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ElectricityPriceAndGreenIndex,
  ) {}

  close() {
    this.dialogRef.close();
  }
}
