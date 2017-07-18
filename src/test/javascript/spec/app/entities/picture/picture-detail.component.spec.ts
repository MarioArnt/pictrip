/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { PictripTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { PictureDetailComponent } from '../../../../../../main/webapp/app/entities/picture/picture-detail.component';
import { PictureService } from '../../../../../../main/webapp/app/entities/picture/picture.service';
import { Picture } from '../../../../../../main/webapp/app/entities/picture/picture.model';

describe('Component Tests', () => {

    describe('Picture Management Detail Component', () => {
        let comp: PictureDetailComponent;
        let fixture: ComponentFixture<PictureDetailComponent>;
        let service: PictureService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [PictripTestModule],
                declarations: [PictureDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    PictureService,
                    JhiEventManager
                ]
            }).overrideTemplate(PictureDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PictureDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PictureService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Picture(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.picture).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
