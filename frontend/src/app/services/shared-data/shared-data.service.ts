import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Appliance } from '../../model/applicance/appliance';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private selectedApplianceSubject: BehaviorSubject<Appliance | null> =
    new BehaviorSubject<Appliance | null>(null);

  setSelectedAppliance(appliance: Appliance) {
    this.selectedApplianceSubject.next(appliance);
  }

  getSelectedAppliance(): Observable<Appliance | null> {
    return this.selectedApplianceSubject.asObservable();
  }
}
