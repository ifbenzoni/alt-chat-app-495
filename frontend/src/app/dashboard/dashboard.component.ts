import { Component } from '@angular/core';
import { UserService } from '../user.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ChatService } from '../chat.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  name: string = '';
  notifications: boolean = false;

  myChats: any[] = [];
  participantChats: any[] = [];
  currentOwnedChat: any = null;
  currentMemberChat: any = null;
  inputChatTitle: string = '';
  addParticipant: string = '';

  chatDisplay: String[] = [];
  currentMessage: string = '';
  chatHistory: String[] = [];
  filterText: string = '';

  socket: WebSocket | null = null;

  constructor(private userService: UserService, private chatService: ChatService) { }
  
  ngOnInit(): void {
    this.checkDetails();
  }

  ngOnDestroy(): void {
    if (this.socket != null) {
      this.socket.send("client disconnected: " + this.name);
      this.socket.close();
    }
  }

  public checkDetails(): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    this.userService.checkDetails(jwt).subscribe({
      next: (response: any) => {
        console.log(response);
        console.log(response.name);
        console.log(response.notifications);
        this.name = response.name;
        this.notifications = response.notifications;
        this.chatDetails(this.name);
      },
      error: (error: HttpErrorResponse) => {
        alert(error.error);
      }
    })
  }

  public editSettings(): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    var setNotifications = !this.notifications;
    this.userService.editSettings(setNotifications, jwt).subscribe({
      next: (response: String) => {
        console.log(response);
        this.checkDetails();
      },
      error: (error: HttpErrorResponse) => {
        alert(error.error);
      }
    })
  }

  public createChat(title: string): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    const chat = JSON.stringify({title: title, owner: this.name, participants: [], history: []});
    this.chatService.createChat(chat, jwt).subscribe({
      next: (response: String) => {
        console.log(response);
        this.chatDetails(this.name);
      },
      error: (error: HttpErrorResponse) => {
        alert(error.error);
      }
    })
  }

  public updateChat(id: BigInteger, title: string, participants: String[], history: String[]): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    participants.push(this.addParticipant);
    console.log(participants);
    const chat = JSON.stringify({id: id, title: title, owner: this.name, participants: participants, history: history});
    this.chatService.createChat(chat, jwt).subscribe({
      next: (response: String) => {
        console.log(response);
        this.addParticipant = '';
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    })
  }

  public chatDetails(name: string): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    this.chatService.chatDetails(name, jwt).subscribe({
      next: (response: any) => {
        console.log(response);
        for (var i = 0; i < response.length; i++) {
          if (response[i].owner == this.name) {
            this.myChats.push(response[i]);
          }
          for (let participant of response[i].participants) {
            if (participant == this.name) {
              this.participantChats.push(response[i]);
            }
          }
        }
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    })
  }

  public connectWS(): void {

    const name = this.name;
    const chatId = this.currentMemberChat.id;
    const token = localStorage.getItem('nameJwt');

    if (this.socket != null) {
      this.socket.close();
    }

    this.socket = new WebSocket(`ws://localhost:8081/ws/chat?chatId=${chatId}&token=${token}`);

    this.socket.onopen = () => {
        console.log("ws connection opened");
        if (this.socket != null) {
          this.socket.send("client connected: " + name);
        }
    };

    this.socket.onmessage = (event) => {
      console.log("Message received: ", event.data);
      console.log(this.chatDisplay.length);
      if (this.chatDisplay.length > 10) {
        this.chatDisplay.shift();
      }
      this.chatDisplay.push(event.data);
      if (event.data.includes("@" + this.name) && this.notifications) {
        alert("notification: new message in chat");
      }
    };
  }

  public sendMessage(message: string): void {
    if (this.socket != null) {
      this.socket.send(this.name + ": " + message);
      var jwt = localStorage.getItem('nameJwt');
      if (jwt == null) {
        jwt = '';
      }
      this.chatService.postToChat(this.currentMemberChat.id, message, jwt).subscribe({
        next: (response: any) => {
          console.log(response);
        },
        error: (error: HttpErrorResponse) => {
          alert(error.message);
        }
      })
      this.currentMessage = '';
    }
  }

  public searchHistory(text: string): void {
    var jwt = localStorage.getItem('nameJwt');
    if (jwt == null) {
      jwt = '';
    }
    this.chatService.search(this.currentMemberChat.id, text, jwt).subscribe({
      next: (response: any) => {
        console.log(response);
        this.chatHistory = [];
        for(let message of response) {
          if (this.chatHistory.length > 10) {
            this.chatHistory.shift();
          }
          this.chatHistory.push(message);
        }
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    })
    this.filterText = '';
  }

}
