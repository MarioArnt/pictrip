import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Journey } from './journey.model';
import { JourneyPopupService } from './journey-popup.service';
import { JourneyService } from './journey.service';
import { Step, StepService } from '../step';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-journey-dialog',
    templateUrl: './journey-dialog.component.html'
})
export class JourneyDialogComponent implements OnInit {

    journey: Journey;
    authorities: any[];
    isSaving: boolean;

    stepfroms: Step[];

    steptos: Step[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private journeyService: JourneyService,
        private stepService: StepService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.stepService
            .query({filter: 'departure-is-null'})
            .subscribe((res: ResponseWrapper) => {
                if (!this.journey.stepFromId) {
                    this.stepfroms = res.json;
                } else {
                    this.stepService
                        .find(this.journey.stepFromId)
                        .subscribe((subRes: Step) => {
                            this.stepfroms = [subRes].concat(res.json);
                        }, (subRes: ResponseWrapper) => this.onError(subRes.json));
                }
            }, (res: ResponseWrapper) => this.onError(res.json));
        this.stepService
            .query({filter: 'arrival-is-null'})
            .subscribe((res: ResponseWrapper) => {
                if (!this.journey.stepToId) {
                    this.steptos = res.json;
                } else {
                    this.stepService
                        .find(this.journey.stepToId)
                        .subscribe((subRes: Step) => {
                            this.steptos = [subRes].concat(res.json);
                        }, (subRes: ResponseWrapper) => this.onError(subRes.json));
                }
            }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.journey.id !== undefined) {
            this.subscribeToSaveResponse(
                this.journeyService.update(this.journey));
        } else {
            this.subscribeToSaveResponse(
                this.journeyService.create(this.journey));
        }
    }

    private subscribeToSaveResponse(result: Observable<Journey>) {
        result.subscribe((res: Journey) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Journey) {
        this.eventManager.broadcast({ name: 'journeyListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    trackStepById(index: number, item: Step) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-journey-popup',
    template: ''
})
export class JourneyPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private journeyPopupService: JourneyPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.journeyPopupService
                    .open(JourneyDialogComponent, params['id']);
            } else {
                this.modalRef = this.journeyPopupService
                    .open(JourneyDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
