// File: header.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base/base.component';
import { UserResponse } from '../../responses/user.response';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, NgbModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent extends BaseComponent implements OnInit {
  userResponse?: UserResponse | null;
  isPopoverOpen = false;
  activeNavItem: number = 0;
  keyword: string = "";

  ngOnInit() {
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
  }

  searchProducts() {
    this.router.navigate(['/products'], {
      queryParams: { keyword: this.keyword }
    });
  }

  togglePopover(event: Event): void {
    event.preventDefault();
    this.isPopoverOpen = !this.isPopoverOpen;
  }

  handleItemClick(index: number): void {
    if (index === 0) {
      this.router.navigate(['/user-profile']);
    } else if (index === 1) {
      this.router.navigate(['/order-history']);
    } else if (index === 2) {
      this.tokenService.removeToken();
      this.userService.removeUserFromLocalStorage();
      this.userResponse = this.userService.getUserResponseFromLocalStorage();
      this.router.navigate(['/']).catch(err => { window.location.href = `${window.location.origin}/#` });
    }
    this.isPopoverOpen = false;
  }

  setActiveNavItem(index: number) {
    this.activeNavItem = index;
  }

}
