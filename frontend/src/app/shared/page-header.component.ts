import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  standalone: true,
  template: `
    <header class="page-header">
      <h1 class="page-title">{{ heading() }}</h1>
      <div class="page-actions">
        <ng-content />
      </div>
    </header>
  `,
})
export class PageHeaderComponent {
  /** Renamed from "title" to avoid template/IDE ambiguity with native title attribute */
  heading = input.required<string>();
}
