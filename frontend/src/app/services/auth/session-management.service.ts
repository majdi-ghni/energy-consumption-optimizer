import { Injectable } from '@angular/core';
import { User } from '../../model/user/user';

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root',
})
export class SessionManagementService {
  constructor() {}

  public saveUser(user: User): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): any {
    const user = localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return {};
  }

  public getAccessToken() {
    return localStorage.getItem('access_token');
  }

  public clearSession(): void {
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem('access_token');
  }

  public isUserLoggedIn(): boolean {
    let user = this.getUser();
    return Object.keys(user).length !== 0;
  }
}
