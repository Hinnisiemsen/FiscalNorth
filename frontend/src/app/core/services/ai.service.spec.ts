import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AiService } from './ai.service';

describe('AiService', () => {
  let service: AiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AiService],
    });
    service = TestBed.inject(AiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getStatus should call assistant status endpoint', () => {
    service.getStatus().subscribe((status) => {
      expect(status.available).toBe(true);
      expect(status.message).toContain('bereit');
    });

    const req = httpMock.expectOne('/api/assistant/status');
    expect(req.request.method).toBe('GET');
    req.flush({
      available: true,
      message: 'Fiscal North ist bereit, deine Fragen zu beantworten.',
    });
  });

  it('chat should post message to assistant chat endpoint', () => {
    service.chat('Wie viel habe ich ausgegeben?').subscribe((response) => {
      expect(response.reply).toBe('Antwort');
      expect(response.proposedActions).toEqual([]);
    });

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
  });
});
