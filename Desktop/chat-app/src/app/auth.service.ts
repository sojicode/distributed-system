import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { auth } from 'firebase/app';
import { User } from 'firebase';
import * as firebase from 'firebase/app';

@Injectable()
export class AuthService {
  user: User;
  constructor(public afAuth: AngularFireAuth, public router: Router) { 
  // constructor(public afAuth: AngularFireAuth) {

  }

  // login(user: string, password: string): boolean {
  //   if (user === 'chandlergegg@gmail.com' && password === 'csc436!') {
  //     localStorage.setItem('username', user);
  //     return true;
  //   }

  //   return false;
  // }

  login(email: string, password: string): boolean {
    try {
      this.afAuth.auth.signInWithEmailAndPassword(email, password)
      // this.router.navigate(['admin/list']);
      localStorage.setItem('username', email);
      return true;
    } catch(e){
      alert("Error!" + e.message);
    }
    return false;
  }

  logout(): any {
    localStorage.removeItem('username');
  }
  // async logout(){
  //   await this.afAuth.auth.signOut();
  //   localStorage.removeItem('user');
  //   this.router.navigate(['admin/login']);
  // }

  getUser(): any {
    return localStorage.getItem('username');
  }

  isLoggedIn(): boolean {
  return this.getUser() !== null;
  }
  
  // isLoggedIn(): boolean {
  //   const user = JSON.parse(localStorage.getItem('user'));
  //   return user !== null;
  // }
}

// this.afAuth.authState.subscribe(user => {
//   if(user) {
//     this.user = user;
//     localStorage.setItem('user', JSON.stringify(this.user));
//   } else {
//     localStorage.setItem('user', null);
//   }
// })

export const AUTH_PROVIDERS: Array<any> = [
  { provide: AuthService, useClass: AuthService }
];
