import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ComponentType } from '@angular/cdk/overlay';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  constructor(private matDialog: MatDialog) {}

  public openDialog(
    component: ComponentType<any>,
    config?: MatDialogConfig<any>,
  ) {
    const reference = this.matDialog.open(component, config);
    return reference.afterClosed();
  }
}
