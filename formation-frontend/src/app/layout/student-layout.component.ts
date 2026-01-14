import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { NavbarComponent } from './navbar.component';

@Component({
  selector: 'app-student-layout',
  standalone: true,
  imports: [NavbarComponent, RouterOutlet],
  templateUrl: './student-layout.component.html',
  styleUrl: './student-layout.component.scss'
})
export class StudentLayoutComponent {}
