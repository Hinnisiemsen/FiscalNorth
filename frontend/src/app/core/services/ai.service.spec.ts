import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AiService, ChatResponse } from './ai.service';
import { ChromeLanguageModelService, PromptAvailability } from './chrome-language-model.service';

describe('AiService', () => {
  let service: AiService;
  let httpMock: HttpTestingController;
  let chrome: {
    availability: jasmine.Spy;
    isReady: jasmine.Spy;
    complete: jasmine.Spy;
    warmUp: jasmine.Spy;
  };

  beforeEach(() => {
    chrome = {
      availability: jasmine
        .createSpy('availability')
        .and.resolveTo('unavailable' as PromptAvailability),
      isReady: jasmine.createSpy('isReady').and.resolveTo(false),
      complete: jasmine.createSpy('complete').and.resolveTo(null),
      warmUp: jasmine.createSpy('warmUp'),
    };
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AiService,
        { provide: ChromeLanguageModelService, useValue: chrome },
      ],
    });
    service = TestBed.inject(AiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getStatus should call assistant status endpoint', (done) => {
    service.getStatus().subscribe((status) => {
      expect(status.available).toBe(true);
      expect(status.message).toContain('bereit');
      expect(status.source).toBe('server');
      done();
    });

    const req = httpMock.expectOne('/api/assistant/status');
    expect(req.request.method).toBe('GET');
    req.flush({
      available: true,
      message: 'Fiscal North ist bereit, deine Fragen zu beantworten.',
    });
  });

  it('getStatus prefers Chrome Prompt API when the on-device model is ready', (done) => {
    chrome.availability.and.resolveTo('available');

    service.getStatus().subscribe((status) => {
      expect(status.available).toBe(true);
      expect(status.source).toBe('chrome');
      expect(status.chromeState).toBe('available');
      expect(chrome.warmUp).not.toHaveBeenCalled();
      done();
    });

    httpMock.expectOne('/api/assistant/status').flush({
      available: false,
      message: 'GEMINI_API_KEY is not configured',
    });
  });

  it('getStatus warms a downloadable Chrome model without blocking chat', (done) => {
    chrome.availability.and.resolveTo('downloadable');

    service.getStatus().subscribe((status) => {
      expect(status.source).toBe('chrome');
      expect(status.chromeState).toBe('downloadable');
      expect(chrome.warmUp).toHaveBeenCalled();
      done();
    });

    httpMock.expectOne('/api/assistant/status').flush({
      available: false,
      message: 'GEMINI_API_KEY is not configured',
    });
  });

  it('chat should post message to assistant chat endpoint', fakeAsync(() => {
    let response: ChatResponse | undefined;
    service.chat('Wie viel habe ich ausgegeben?').subscribe((res) => {
      response = res;
    });
    tick();

    const req = httpMock.expectOne('/api/assistant/chat');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      message: 'Wie viel habe ich ausgegeben?',
      conversationId: undefined,
    });
    req.flush({
      reply: 'Antwort',
      proposedActions: [],
      followUpRecommendations: [],
      conversationId: 'conv-1',
    });
    tick();
    expect(response?.reply).toBe('Antwort');
    expect(response?.proposedActions).toEqual([]);
  }));

  it('chat uses Chrome Prompt API and skips the server chat endpoint', fakeAsync(() => {
    chrome.isReady.and.resolveTo(true);
    const chromeReply: ChatResponse = {
      reply: '268 € Lebensmittel',
      proposedActions: [],
      followUpRecommendations: ['Wie stehen die Budgets?'],
      conversationId: 'chrome-nano',
    };
    chrome.complete.and.resolveTo(chromeReply);

    let response: ChatResponse | undefined;
    service.chat('Wie viel für Lebensmittel?').subscribe((res) => {
      response = res;
    });
    tick();

    const ctx = httpMock.expectOne('/api/assistant/context');
    expect(ctx.request.method).toBe('GET');
    ctx.flush({ snapshot: 'Groceries 268 EUR' });
    tick();

    expect(response).toEqual(chromeReply);
    expect(chrome.complete).toHaveBeenCalled();
  }));
});
