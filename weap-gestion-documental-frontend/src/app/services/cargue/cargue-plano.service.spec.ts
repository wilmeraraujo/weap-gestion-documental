import { TestBed } from '@angular/core/testing';

import { CarguePlanoService } from './cargue-plano.service';

describe('CarguePlanoService', () => {
  let service: CarguePlanoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CarguePlanoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
