import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SeasonService } from './core/theme/season.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');

  constructor() {
    inject(SeasonService).init();
  }
}
