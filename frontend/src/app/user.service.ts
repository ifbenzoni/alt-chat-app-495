import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiServerUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  public createAcc(userJSON: string): Observable<string> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8'});
    return this.http.post<string>(`${this.apiServerUrl}/user/create`, userJSON, {headers: headers});
  }

  public login(userJSON: string): Observable<string> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8'});
    return this.http.post<string>(`${this.apiServerUrl}/user/login`, userJSON, {headers: headers});
  }

  public checkDetails(token: string): Observable<[]> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.get<[]>(`${this.apiServerUrl}/user/userDetails`, {headers: headers});
  }

  public editSettings(setNotifications: boolean, token: string): Observable<String> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.post<String>(`${this.apiServerUrl}/user/settings`, setNotifications, {headers: headers});
  }

}