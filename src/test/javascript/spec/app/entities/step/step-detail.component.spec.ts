/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { PictripTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { StepDetailComponent } from '../../../../../../main/webapp/app/entities/step/step-detail.component';
import { StepService } from '../../../../../../main/webapp/app/entities/step/step.service';
import { Step } from '../../../../../../main/webapp/app/entities/step/step.model';

describe('Component Tests', () => {

    describe('Step Management Detail Component', () => {
        let comp: StepDetailComponent;
        let fixture: ComponentFixture<StepDetailComponent>;
        let service: StepService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [PictripTestModule],
                declarations: [StepDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    StepService,
                    JhiEventManager
                ]
            }).overrideTemplate(StepDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(StepDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(StepService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Step(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.step).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
