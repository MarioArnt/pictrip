/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { PictripTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { JourneyDetailComponent } from '../../../../../../main/webapp/app/entities/journey/journey-detail.component';
import { JourneyService } from '../../../../../../main/webapp/app/entities/journey/journey.service';
import { Journey } from '../../../../../../main/webapp/app/entities/journey/journey.model';

describe('Component Tests', () => {

    describe('Journey Management Detail Component', () => {
        let comp: JourneyDetailComponent;
        let fixture: ComponentFixture<JourneyDetailComponent>;
        let service: JourneyService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [PictripTestModule],
                declarations: [JourneyDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    JourneyService,
                    JhiEventManager
                ]
            }).overrideTemplate(JourneyDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(JourneyDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(JourneyService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Journey(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.journey).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
