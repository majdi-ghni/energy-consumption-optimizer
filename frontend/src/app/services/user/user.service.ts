import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment/environment';
import { User } from '../../model/user/user';

const apiUrl = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getUser(id: string) {
    return this.http.get<User>(`${apiUrl}/user/get/${id}`);
  }

  getUserByUsername(username: string) {
    return this.http.get<User>(`${apiUrl}/user/byUsername/${username}`);
  }

  createUser(user: User) {
    return this.http.post<string>(`${apiUrl}/user/create`, user, {
      responseType: 'text' as 'json',
    });
  }
}
