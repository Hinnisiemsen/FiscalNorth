import { Injectable } from '@angular/core';
import type { ChatResponse, ProposedAction, ProposedActionType } from './ai.service';

export type PromptAvailability = 'unavailable' | 'downloadable' | 'downloading' | 'available';

interface LanguageModelSession {
  prompt(input: string, options?: Record<string, unknown>): Promise<string>;
  destroy?: () => void;
}

interface LanguageModelStatic {
  availability(options?: Record<string, unknown>): Promise<PromptAvailability>;
  create(options?: Record<string, unknown>): Promise<LanguageModelSession>;
}

const ACTION_TYPES: ProposedActionType[] = [
  'CREATE_BUDGET',
  'CREATE_CATEGORY',
  'CREATE_TRANSACTION',
  'CREATE_GOAL',
];

const CREATE_TIMEOUT_MS = 15_000;
const PROMPT_TIMEOUT_MS = 20_000;

/**
 * Chrome Prompt API (Gemini Nano). Create with no language/modality options so
 * Chrome does not look up a missing feature execution config
 * (`kConfigNotAvailableForFeature`). Chat waits on the model only after
 * `availability()` is `available`; a downloadable model is warmed in the
 * background so the first user turn can use the server fallback.
 */
@Injectable({ providedIn: 'root' })
export class ChromeLanguageModelService {
  private session: LanguageModelSession | null = null;
  private sessionPromise: Promise<LanguageModelSession | null> | null = null;

  async availability(): Promise<PromptAvailability> {
    const api = this.api();
    if (!api) {
      return 'unavailable';
    }
    try {
      return await api.availability();
    } catch {
      return 'unavailable';
    }
  }

  async isReady(): Promise<boolean> {
    return (await this.availability()) === 'available';
  }

  /** Start Gemini Nano download without blocking the current chat turn. */
  warmUp(): void {
    void this.createSession(false);
  }

  async complete(
    userMessage: string,
    snapshot: string,
    locale: string,
  ): Promise<ChatResponse | null> {
    const session = await this.createSession(true);
    if (!session) {
      return null;
    }
    try {
      const raw = await this.withTimeout(
        session.prompt(this.buildPrompt(userMessage, snapshot, locale)),
        PROMPT_TIMEOUT_MS,
      );
      return this.parse(raw);
    } catch (err) {
      console.warn('Chrome language model prompt failed', err);
      this.resetSession();
      return null;
    }
  }

  private api(): LanguageModelStatic | undefined {
    const root = globalThis as unknown as {
      LanguageModel?: LanguageModelStatic;
      ai?: { languageModel?: LanguageModelStatic };
    };
    return root.LanguageModel ?? root.ai?.languageModel;
  }

  private async createSession(requireReady: boolean): Promise<LanguageModelSession | null> {
    if (this.session) {
      return this.session;
    }
    if (requireReady && (await this.availability()) !== 'available') {
      return null;
    }
    if (!this.sessionPromise) {
      this.sessionPromise = this.openSession().then((session) => {
        this.session = session;
        this.sessionPromise = null;
        return session;
      });
    }
    try {
      if (requireReady) {
        return await this.withTimeout(this.sessionPromise, CREATE_TIMEOUT_MS);
      }
      return await this.sessionPromise;
    } catch (err) {
      console.warn('Chrome language model session failed', err);
      return null;
    }
  }

  private async openSession(): Promise<LanguageModelSession | null> {
    const api = this.api();
    if (!api) {
      return null;
    }
    try {
      const state = await api.availability();
      if (state === 'unavailable') {
        return null;
      }
      // Empty options: language/modality configs are what trigger
      // "execution config available for the feature" on some Chrome builds.
      return await api.create();
    } catch (err) {
      console.warn('Chrome language model session failed', err);
      return null;
    }
  }

  private withTimeout<T>(promise: Promise<T>, ms: number): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      const timer = setTimeout(
        () => reject(new Error(`Chrome language model timed out after ${ms}ms`)),
        ms,
      );
      promise.then(
        (value) => {
          clearTimeout(timer);
          resolve(value);
        },
        (err) => {
          clearTimeout(timer);
          reject(err);
        },
      );
    });
  }

  private resetSession(): void {
    try {
      this.session?.destroy?.();
    } catch {
      // ignore
    }
    this.session = null;
    this.sessionPromise = null;
  }

  private buildPrompt(userMessage: string, snapshot: string, locale: string): string {
    const lang = locale.startsWith('de')
      ? 'Deutsch'
      : locale.startsWith('fr')
        ? 'Französisch'
        : locale.startsWith('es')
          ? 'Spanisch'
          : 'English';
    return `You are Fiscal North, a household finance assistant. Speak as "we". Never mention Google, Gemini, Chrome, or model names. Reply in ${lang}. Use ONLY the finance data below. Do not change data; propose actions only.

Finance data:
${snapshot || '(none)'}

Return ONLY valid JSON:
{"reply":"...","actions":[{"type":"CREATE_BUDGET|CREATE_CATEGORY|CREATE_TRANSACTION|CREATE_GOAL","summary":"...","payload":{}}],"followUpRecommendations":["...","..."]}

User: ${userMessage}`;
  }

  private parse(raw: string): ChatResponse {
    const json = this.extractJson(raw);
    try {
      const root = JSON.parse(json) as {
        reply?: string;
        actions?: Array<{ type?: string; summary?: string; payload?: Record<string, unknown> }>;
        followUpRecommendations?: string[];
        followUps?: string[];
      };
      const actions: ProposedAction[] = (root.actions ?? [])
        .filter((a) => ACTION_TYPES.includes(a.type as ProposedActionType))
        .map((a) => ({
          type: a.type as ProposedActionType,
          summary: a.summary ?? '',
          payload: a.payload ?? {},
        }));
      const followUps = (root.followUpRecommendations ?? root.followUps ?? []).filter(
        (item) => typeof item === 'string' && item.trim(),
      );
      return {
        reply: root.reply?.trim() || raw.trim(),
        proposedActions: actions,
        followUpRecommendations: followUps,
        conversationId: 'chrome-nano',
      };
    } catch {
      return {
        reply: raw.trim(),
        proposedActions: [],
        followUpRecommendations: [],
        conversationId: 'chrome-nano',
      };
    }
  }

  private extractJson(raw: string): string {
    const fenced = raw.match(/```(?:json)?\s*([\s\S]*?)```/i);
    if (fenced?.[1]) {
      return fenced[1].trim();
    }
    const start = raw.indexOf('{');
    const end = raw.lastIndexOf('}');
    if (start >= 0 && end > start) {
      return raw.slice(start, end + 1);
    }
    return raw.trim();
  }
}
