import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';

import { UserService } from '../user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  inputNameAcc: string = '';
  inputPasswordAcc: string = '';
  inputName: string = '';
  inputPassword: string = '';

  public hasNameJwt: boolean = false;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.hasNameJwt = localStorage.getItem('nameJwt') !== null;
  }

  public createAcc(name: string, password: string): void {
    const user = JSON.stringify({name: name, pw: password});
    this.userService.createAcc(user).subscribe({
      next: (response: string) => {
        console.log(response);
        alert(response);
        this.inputNameAcc = '';
        this.inputPasswordAcc = '';
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    })
  }

  public login(name: string, password: string): void {
    const user = JSON.stringify({name: name, pw: password});
    this.userService.login(user).subscribe({
      next: (response: string) => {
        localStorage.setItem('nameJwt', response);
        console.log(response);
        this.hasNameJwt = localStorage.getItem('nameJwt') !== null;
        this.inputName = '';
        this.inputPassword = '';
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    })
  }

  public logout(): void {
    localStorage.removeItem('nameJwt');
    this.hasNameJwt = localStorage.getItem('nameJwt') !== null;
  }

}
