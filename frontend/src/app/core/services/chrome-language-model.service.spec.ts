import { ChromeLanguageModelService } from './chrome-language-model.service';

describe('ChromeLanguageModelService', () => {
  let service: ChromeLanguageModelService;

  beforeEach(() => {
    service = new ChromeLanguageModelService();
    delete (globalThis as { LanguageModel?: unknown }).LanguageModel;
  });

  it('reports unavailable when Prompt API is missing', async () => {
    expect(await service.availability()).toBe('unavailable');
    expect(await service.isReady()).toBe(false);
    expect(await service.complete('hello', '', 'en')).toBeNull();
  });

  it('does not treat a downloadable model as ready for chat', async () => {
    const create = jasmine.createSpy('create').and.returnValue(new Promise(() => undefined));
    (globalThis as { LanguageModel?: unknown }).LanguageModel = {
      availability: () => Promise.resolve('downloadable'),
      create,
    };
    expect(await service.isReady()).toBe(false);
    expect(create).not.toHaveBeenCalled();
    service.warmUp();
    await new Promise((resolve) => setTimeout(resolve, 0));
    expect(create).toHaveBeenCalledWith();
  });

  it('prompts Chrome LanguageModel with empty create options', async () => {
    const prompt = jasmine
      .createSpy('prompt')
      .and.resolveTo('{"reply":"268 € groceries","actions":[],"followUpRecommendations":[]}');
    const create = jasmine.createSpy('create').and.resolveTo({ prompt, destroy: () => undefined });
    const availability = jasmine.createSpy('availability').and.resolveTo('available');
    (globalThis as { LanguageModel?: unknown }).LanguageModel = { availability, create };

    const result = await service.complete('How much on groceries?', 'Groceries 268 EUR', 'en');

    expect(create).toHaveBeenCalledWith();
    expect(result?.reply).toContain('268');
    expect(result?.conversationId).toBe('chrome-nano');

    await service.complete('And transport?', 'Transport 40 EUR', 'en');
    expect(create).toHaveBeenCalledTimes(1);
  });
});
