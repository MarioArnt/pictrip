import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Step } from './step.model';
import { StepPopupService } from './step-popup.service';
import { StepService } from './step.service';
import { Place, PlaceService } from '../place';
import { Trip, TripService } from '../trip';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-step-dialog',
    templateUrl: './step-dialog.component.html'
})
export class StepDialogComponent implements OnInit {

    step: Step;
    authorities: any[];
    isSaving: boolean;

    places: Place[];

    trips: Trip[];
    dateFromDp: any;
    dateToDp: any;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private stepService: StepService,
        private placeService: PlaceService,
        private tripService: TripService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.placeService
            .query({filter: 'step-is-null'})
            .subscribe((res: ResponseWrapper) => {
                if (!this.step.placeId) {
                    this.places = res.json;
                } else {
                    this.placeService
                        .find(this.step.placeId)
                        .subscribe((subRes: Place) => {
                            this.places = [subRes].concat(res.json);
                        }, (subRes: ResponseWrapper) => this.onError(subRes.json));
                }
            }, (res: ResponseWrapper) => this.onError(res.json));
        this.tripService.query()
            .subscribe((res: ResponseWrapper) => { this.trips = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.step.id !== undefined) {
            this.subscribeToSaveResponse(
                this.stepService.update(this.step));
        } else {
            this.subscribeToSaveResponse(
                this.stepService.create(this.step));
        }
    }

    private subscribeToSaveResponse(result: Observable<Step>) {
        result.subscribe((res: Step) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Step) {
        this.eventManager.broadcast({ name: 'stepListModification', content: 'OK'});
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

    trackPlaceById(index: number, item: Place) {
        return item.id;
    }

    trackTripById(index: number, item: Trip) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-step-popup',
    template: ''
})
export class StepPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private stepPopupService: StepPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.stepPopupService
                    .open(StepDialogComponent, params['id']);
            } else {
                this.modalRef = this.stepPopupService
                    .open(StepDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
