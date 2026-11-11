import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Dashboard } from '../../core/models';

@Component({
  imports: [MatIconModule],
  selector: 'app-dashboard-stats',
  styleUrl: './dashboard-stats.css',
  templateUrl: './dashboard-stats.html',
})
export class DashboardStats {
  readonly dados = input.required<Dashboard>();
}
