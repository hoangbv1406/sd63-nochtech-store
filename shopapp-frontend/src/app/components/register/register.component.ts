// File: register.component.ts
import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base/base.component';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiResponse } from '../../responses/api.response';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent extends BaseComponent {
  @ViewChild('registerForm') registerForm!: NgForm;

  phoneNumber: string = '';
  email: string = '';
  password: string = '';
  retypePassword: string = '';
  fullName: string = '';
  address: string = '';
  isAccepted: boolean = true;
  dateOfBirth: Date = new Date();
  dateOfBirthString: string = '';
  showPassword: boolean = false;

  constructor() {
    super();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 18);
    this.dateOfBirthString = this.toInputDate(this.dateOfBirth);
  }

  private toInputDate(d: Date): string {
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }

  onDateChange(value: string) {
    this.dateOfBirthString = value;
    this.dateOfBirth = value ? new Date(value + 'T00:00:00') : new Date();
    this.checkAge();
  }

  register() {
    if (this.registerForm && this.registerForm.invalid) {
      this.toastService.showToast({
        error: null,
        defaultMsg: 'Vui lòng kiểm tra lại thông tin.',
        title: 'Lỗi xác thực'
      });
      return;
    }
    if (this.password !== this.retypePassword) {
      this.toastService.showToast({
        error: null,
        defaultMsg: 'Mật khẩu không khớp.',
        title: 'Lỗi Đăng Ký'
      });
      return;
    }

    const dobString = this.dateOfBirthString;
    const registerDTO: any = {
      fullname: this.fullName,
      phone_number: this.phoneNumber,
      email: this.email,
      address: this.address,
      password: this.password,
      retype_password: this.retypePassword,
      date_of_birth: dobString,
      facebook_account_id: 0,
      google_account_id: 0,
      role_id: 1
    };

    console.log('Register payload', registerDTO);

    this.userService.register(registerDTO).subscribe({
      next: (apiResponse: ApiResponse) => {
        const confirmation = window.confirm('Đăng ký thành công, mời bạn đăng nhập. Bấm "OK" để chuyển đến trang đăng nhập.');
        if (confirmation) {
          this.router.navigate(['/login']);
        }
      },
      error: (error: HttpErrorResponse) => {
        const msg = error.error?.message || 'Lỗi không xác định';
        this.toastService.showToast({
          error: error,
          defaultMsg: msg,
          title: 'Lỗi Đăng Ký'
        });
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  checkPasswordsMatch() {
    if (!this.registerForm) return;
    const retypeCtrl = this.registerForm.form.get('retypePassword');
    if (!retypeCtrl) return;

    if (this.password !== this.retypePassword) {
      const errs = { ...(retypeCtrl.errors ?? {}) };
      errs['passwordMismatch'] = true;
      retypeCtrl.setErrors(errs);
    } else {
      const errs = { ...(retypeCtrl.errors ?? {}) };
      delete errs['passwordMismatch'];
      const hasOther = Object.keys(errs).length > 0;
      retypeCtrl.setErrors(hasOther ? errs : null);
    }
  }

  checkAge() {
    if (!this.registerForm) return;
    if (!this.dateOfBirth) return;
    const today = new Date();
    const birthDate = new Date(this.dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) { age-- }
    const dobCtrl = this.registerForm.form.get('dateOfBirth');
    if (!dobCtrl) return;
    if (age < 18) {
      const errs = { ...(dobCtrl.errors ?? {}) };
      errs['invalidAge'] = true;
      dobCtrl.setErrors(errs);
    } else {
      const errs = { ...(dobCtrl.errors ?? {}) };
      delete errs['invalidAge'];
      const hasOther = Object.keys(errs).length > 0;
      dobCtrl.setErrors(hasOther ? errs : null);
    }
  }

}
