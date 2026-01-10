import { TestBed } from '@angular/core/testing';

import { PlanningPistes } from './planning-pistes';

describe('PlanningPistes', () => {
  let service: PlanningPistes;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlanningPistes);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
