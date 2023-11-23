import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SessionManagementService } from './session-management.service';
import { environment } from '../../environment/environment';

const apiUrl = environment.apiUrl;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private tokenService: SessionManagementService,
  ) {}

  login(username: string, password: any): Observable<any> {
    return this.http.post(
      apiUrl + '/api/v1/auth/authenticate',
      {
        username,
        password,
      },
      httpOptions,
    );
  }

  logout(): Observable<any> {
    return this.http.get(apiUrl + '/auth/logout', {});
  }
}
