import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarguePlanoComponent } from './cargue-plano.component';

describe('CarguePlanoComponent', () => {
  let component: CarguePlanoComponent;
  let fixture: ComponentFixture<CarguePlanoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarguePlanoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarguePlanoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
