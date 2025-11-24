import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarguePlanoFormComponent } from './cargue-plano-form.component';

describe('CarguePlanoFormComponent', () => {
  let component: CarguePlanoFormComponent;
  let fixture: ComponentFixture<CarguePlanoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarguePlanoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarguePlanoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
