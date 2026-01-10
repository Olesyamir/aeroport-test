import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoriqueList } from './historique-list';

describe('HistoriqueList', () => {
  let component: HistoriqueList;
  let fixture: ComponentFixture<HistoriqueList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoriqueList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoriqueList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
