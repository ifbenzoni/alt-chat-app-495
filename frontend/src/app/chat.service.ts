import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private apiServerUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) { }

  public createChat(chatJSON: string, token: string): Observable<String> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.post<String>(`${this.apiServerUrl}/chat/create`, chatJSON, {headers: headers});
  }

  public chatDetails(name: string, token: string): Observable<[]> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.get<[]>(`${this.apiServerUrl}/chat/chatDetails/${name}`, {headers: headers});
  }

  public postToChat(id: BigInteger, text: string, token: string): Observable<[]> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.post<[]>(`${this.apiServerUrl}/chat/add/${id}`, text, {headers: headers});
  }

  public search (id: BigInteger, text: string, token: string): Observable<[]> {
    let headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8', 'token': token});
    return this.http.get<[]>(`${this.apiServerUrl}/chat/search/${id}/${text}`, {headers: headers});
  }

}