import { Component } from '@angular/core';

@Component({
  selector: 'app-glass-card',
  standalone: true,
  template: `
    <div class="card-glass">
      <ng-content />
    </div>
  `,
})
export class GlassCardComponent {}
