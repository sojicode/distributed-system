import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from "@angular/common/http";
import { UsersService } from './user/users.service';
import { ThreadsService } from './thread/threads.service';
import { MessagesService } from './message/messages.service';
import { AppComponent } from './app.component';
import { ChatMessageComponent } from './chat-message/chat-message.component';
import { ChatThreadComponent } from './chat-thread/chat-thread.component';
import { ChatNavBarComponent } from './chat-nav-bar/chat-nav-bar.component';
import { ChatThreadsComponent } from './chat-threads/chat-threads.component';
import { ChatWindowComponent } from './chat-window/chat-window.component';
import { ChatPageComponent } from './chat-page/chat-page.component';
import { FromNowPipe } from './pipes/from-now.pipe';
import { HomeComponent } from './home/home.component'
import { ContactComponent } from './contact/contact.component';
import { AboutComponent } from './about/about.component';
import { LoginComponent } from './login/login.component';
import { ProtectedComponent } from './protected/protected.component';
import { ProductsComponent } from './products/products.component';
import { AUTH_PROVIDERS } from './auth.service';
import { LoggedInGuard } from './logged-in.guard';
import { RouterModule, Routes } from '@angular/router';
import { routes as childRoutes, ProductsModule } from './products/products.module';

import { AngularFireModule } from '@angular/fire';
import { AngularFirestoreModule } from '@angular/fire/firestore';
import { AngularFireStorageModule } from '@angular/fire/storage';
import { AngularFireAuthModule } from '@angular/fire/auth';
import { environment } from '../environments/environment';


const routes: Routes = [
  // basic routes
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'contactus', redirectTo: 'contact' },

  // authentication demo
  { path: 'login', component: LoginComponent},

  {
    path: 'protected',
    component: ProtectedComponent, canActivate: [ LoggedInGuard ]
  },

  // nested
  {
    path: 'products',
    component: ProductsComponent,
    children: childRoutes
  },
];
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ChatMessageComponent,
    ChatThreadComponent,
    ChatNavBarComponent,
    ChatThreadsComponent,
    ChatWindowComponent,
    ChatPageComponent,
    FromNowPipe,
    ContactComponent,
    AboutComponent,
    LoginComponent,
    ProtectedComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes), 
    ProductsModule,

    AngularFireModule,
    AngularFirestoreModule,
    AngularFireStorageModule,
    AngularFireAuthModule,

// Initialize Firebase
    AngularFireModule.initializeApp(environment.firebase, 'chat-app')
  // firebase.analytics();
  ],
  providers: [
    MessagesService, ThreadsService, UsersService, 
    AUTH_PROVIDERS,
    LoggedInGuard
  ],

  bootstrap: [AppComponent]
})
export class AppModule { }
