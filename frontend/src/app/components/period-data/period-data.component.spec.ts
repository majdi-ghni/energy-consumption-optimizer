import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodDataComponent } from './period-data.component';

describe('PeriodDataComponent', () => {
  let component: PeriodDataComponent;
  let fixture: ComponentFixture<PeriodDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PeriodDataComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PeriodDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
