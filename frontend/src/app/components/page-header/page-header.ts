import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  imports: [MatIconModule, RouterLink],
  selector: 'app-page-header',
  styleUrl: './page-header.css',
  templateUrl: './page-header.html',
})
export class PageHeader {
  readonly eyebrow = input('');
  readonly titulo = input.required<string>();
  readonly subtitulo = input('');
  readonly voltarLink = input<string | null>(null);
  readonly voltarRotulo = input('Voltar');
}
