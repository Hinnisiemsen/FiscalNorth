import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type ProposedActionType = 'CREATE_BUDGET' | 'CREATE_CATEGORY' | 'CREATE_TRANSACTION';

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
}

@Injectable({ providedIn: 'root' })
export class AiService {
  constructor(private api: ApiService) {}

  getStatus(): Observable<AssistantStatus> {
    return this.api.get<AssistantStatus>('/assistant/status');
  }

  chat(message: string, conversationId?: string): Observable<ChatResponse> {
    return this.api.post<ChatResponse>('/assistant/chat', { message, conversationId });
  }
}
