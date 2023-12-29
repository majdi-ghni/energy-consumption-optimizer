import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsagePlanesComponent } from './usage-planes.component';

describe('UsagePlanesComponent', () => {
  let component: UsagePlanesComponent;
  let fixture: ComponentFixture<UsagePlanesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsagePlanesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UsagePlanesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
