import { Injectable, inject } from '@angular/core';
import { Observable, from, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { firstValueFrom } from 'rxjs';
import { ApiService } from './api.service';
import { ChromeLanguageModelService, PromptAvailability } from './chrome-language-model.service';

export type ProposedActionType =
  'CREATE_BUDGET' | 'CREATE_CATEGORY' | 'CREATE_TRANSACTION' | 'CREATE_GOAL';

export interface ProposedAction {
  type: ProposedActionType;
  summary: string;
  payload: Record<string, unknown>;
}

export interface ChatResponse {
  reply: string;
  proposedActions: ProposedAction[];
  followUpRecommendations: string[];
  conversationId: string;
}

export interface AssistantStatus {
  available: boolean;
  message: string;
  demo?: boolean;
  source?: 'server' | 'chrome';
  chromeState?: PromptAvailability;
}

@Injectable({ providedIn: 'root' })
export class AiService {
  private readonly chromeModel = inject(ChromeLanguageModelService);

  constructor(private api: ApiService) {}

  getStatus(): Observable<AssistantStatus> {
    const serverStatus$ = this.api
      .get<AssistantStatus>('/assistant/status')
      .pipe(catchError(() => of({ available: false, message: '', demo: false })));
    return serverStatus$.pipe(
      switchMap((status) =>
        from(this.chromeModel.availability()).pipe(
          map((chrome) => {
            if (chrome === 'available' || chrome === 'downloadable' || chrome === 'downloading') {
              if (chrome !== 'available') {
                this.chromeModel.warmUp();
              }
              return {
                available: true,
                demo: false,
                source: 'chrome' as const,
                chromeState: chrome,
                message: status.message,
              };
            }
            return { ...status, source: 'server' as const };
          }),
        ),
      ),
    );
  }

  chat(message: string, conversationId?: string): Observable<ChatResponse> {
    return from(this.tryChromeChat(message)).pipe(
      switchMap((chromeReply) => {
        if (chromeReply) {
          return of(chromeReply);
        }
        return this.api.post<ChatResponse>('/assistant/chat', { message, conversationId });
      }),
    );
  }

  private async tryChromeChat(message: string): Promise<ChatResponse | null> {
    if (!(await this.chromeModel.isReady())) {
      return null;
    }
    let snapshot = '';
    try {
      const ctx = await firstValueFrom(this.api.get<{ snapshot: string }>('/assistant/context'));
      snapshot = ctx?.snapshot ?? '';
    } catch {
      snapshot = '';
    }
    const locale = typeof document !== 'undefined' ? document.documentElement.lang || 'en' : 'en';
    return this.chromeModel.complete(message, snapshot, locale);
  }
}
