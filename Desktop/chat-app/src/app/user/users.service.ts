import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';
import { User } from './user.model';

import { AngularFirestore } from '@angular/fire/firestore';
import { AngularFireAuth } from '@angular/fire/auth';
import * as firebase from 'firebase/app';


/**
 * UserService manages our current user
 */
@Injectable()
export class UsersService {
  // `currentUser` contains the current user
  currentUser: Subject<User> = new BehaviorSubject<User>(null);

  public setCurrentUser(newUser: User): void {
    this.currentUser.next(newUser);
  }
}

export const userServiceInjectables: Array<any> = [
  UsersService
];
