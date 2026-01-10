import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanningPistesList } from './planning-pistes-list';

describe('PlanningPistesList', () => {
  let component: PlanningPistesList;
  let fixture: ComponentFixture<PlanningPistesList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanningPistesList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanningPistesList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
