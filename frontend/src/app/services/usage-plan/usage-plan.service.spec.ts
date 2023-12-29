import { TestBed } from '@angular/core/testing';

import { UsagePlanService } from './usage-plan.service';

describe('UsagePlanService', () => {
  let service: UsagePlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsagePlanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
