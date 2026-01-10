import { TestBed } from '@angular/core/testing';

import { Avion } from './avion';

describe('Avion', () => {
  let service: Avion;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Avion);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
