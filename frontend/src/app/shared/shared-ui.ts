import { PageHeaderComponent } from './page-header.component';
import { GlassCardComponent } from './glass-card.component';

/** Re-export shared UI standalone components for consistent @Component.imports */
export { PageHeaderComponent, GlassCardComponent };

/** Spread into feature component imports: imports: [CommonModule, ...PAGE_HEADER_IMPORTS] */
export const PAGE_HEADER_IMPORTS = [PageHeaderComponent] as const;

export const GLASS_CARD_IMPORTS = [GlassCardComponent] as const;
