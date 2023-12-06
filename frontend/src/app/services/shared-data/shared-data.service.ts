import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Appliance } from '../../model/applicance/appliance';
import { User } from '../../model/user/user';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private selectedApplianceSubject: BehaviorSubject<Appliance | null> =
    new BehaviorSubject<Appliance | null>(null);
  private loggedUserSubject: BehaviorSubject<User | null> =
    new BehaviorSubject<User | null>(null);

  setSelectedAppliance(appliance: Appliance) {
    this.selectedApplianceSubject.next(appliance);
  }

  getSelectedAppliance(): Observable<Appliance | null> {
    return this.selectedApplianceSubject.asObservable();
  }

  setLoggedUser(user: User) {
    console.log(user);
    this.loggedUserSubject.next(user);
  }

  getLoggedUser(): Observable<User | null> {
    return this.loggedUserSubject.asObservable();
  }
}
