import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-detail.product.admin',
  standalone: true,
  templateUrl: './update.product.admin.component.html',
  styleUrls: ['./update.product.admin.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class UpdateProductAdminComponent {
}
