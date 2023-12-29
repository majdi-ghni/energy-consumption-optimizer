import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElectricityDataComponent } from './electricity-data.component';

describe('ElectricityDataComponent', () => {
  let component: ElectricityDataComponent;
  let fixture: ComponentFixture<ElectricityDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ElectricityDataComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ElectricityDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
