import { TestBed } from '@angular/core/testing';

import { ElectricityDataService } from './electricity-data.service';

describe('ElectricityDataService', () => {
  let service: ElectricityDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ElectricityDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
