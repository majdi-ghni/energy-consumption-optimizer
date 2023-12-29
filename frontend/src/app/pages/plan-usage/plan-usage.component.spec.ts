import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanUsageComponent } from './plan-usage.component';

describe('PlanUsageComponent', () => {
  let component: PlanUsageComponent;
  let fixture: ComponentFixture<PlanUsageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanUsageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PlanUsageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
