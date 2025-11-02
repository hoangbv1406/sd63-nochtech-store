import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-insert.product.admin',
  standalone: true,
  templateUrl: './insert.product.admin.component.html',
  styleUrls: ['./insert.product.admin.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class InsertProductAdminComponent {
}
