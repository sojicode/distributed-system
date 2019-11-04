import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase/app';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

@Injectable({
  providedIn: 'root'
})

export class LoginComponent {
  message: string;

  constructor(public authService: AuthService, private router: Router) {
    this.message = '';
  
  }

  login(username: string, password: string): boolean {
    this.message = '';
    if (!this.authService.login(username, password)) {
      this.message = 'Incorrect credentials.';
      setTimeout(function() {
        this.message = '';
      }.bind(this), 2500);
    }
    else{
      this.router.navigateByUrl('/protected');
    }
    return false;
  }

  logout(): boolean {
    this.authService.logout();
    return false;
  }

}
