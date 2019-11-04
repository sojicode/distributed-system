import { Component, Inject, Input } from '@angular/core';
import { ChatExampleData } from './data/chat-example-data';
import { UsersService } from './user/users.service';
import { ThreadsService } from './thread/threads.service';
import { MessagesService } from './message/messages.service';
import { Router } from '@angular/router';

import { AngularFirestore } from '@angular/fire/firestore';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
    @Input()isDirect: boolean;
    
    constructor(private router: Router, public messagesService: MessagesService,
              public threadsService: ThreadsService,
              public usersService: UsersService) {
    ChatExampleData.init(messagesService, threadsService, usersService);
    this.isDirect = false;
  }
}
