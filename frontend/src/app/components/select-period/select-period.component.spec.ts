import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectPeriodComponent } from './select-period.component';

describe('SelectPeriodComponent', () => {
  let component: SelectPeriodComponent;
  let fixture: ComponentFixture<SelectPeriodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectPeriodComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectPeriodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
