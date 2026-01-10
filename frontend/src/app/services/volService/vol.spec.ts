import { TestBed } from '@angular/core/testing';

import { Vol } from './vol';

describe('Vol', () => {
  let service: Vol;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Vol);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
