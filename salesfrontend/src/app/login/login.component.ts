import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule , FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  mail = '';
  password = '';
  message = '';
  isError = false;

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const role = localStorage.getItem('role');

    if (isLoggedIn === 'true' && role) {
      if (role === 'ADMIN') {
        this.router.navigate(['/admin']);
      } else {
        this.router.navigate(['/user']);
      }
    }
  }
  
  login() {
    this.message = '';
    this.isError = false;

    // basic validation (frontend)
    if (!this.mail || !this.password) {
      this.isError = true;
      this.message = 'Email and password are required';
      return;
    }

    const payload = {
      mail: this.mail,
      password: this.password
    };

    this.http.post<any>(`${this.baseUrl}/login`, payload)
      .subscribe({
        next: (res) => {
          if (res.success) {
              localStorage.setItem('isLoggedIn', 'true');
              localStorage.setItem('role', res.data.role);
                 localStorage.setItem('mail', res.data.mail);
            this.isError = false;
            this.message = res.message || 'Login successful';

            if (res.data.role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/user']);
          }

          }

          else {
            this.isError = true;
            this.message = res.message || 'Wrong credentials';
          }
        },
        error: () => {
          this.isError = true;
          this.message = 'Backend error. Please try again.';
        }
      });
  }
}
