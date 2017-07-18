import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Picture } from './picture.model';
import { PicturePopupService } from './picture-popup.service';
import { PictureService } from './picture.service';
import { Place, PlaceService } from '../place';
import { Step, StepService } from '../step';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-picture-dialog',
    templateUrl: './picture-dialog.component.html'
})
export class PictureDialogComponent implements OnInit {

    picture: Picture;
    authorities: any[];
    isSaving: boolean;

    places: Place[];

    steps: Step[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private pictureService: PictureService,
        private placeService: PlaceService,
        private stepService: StepService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.placeService.query()
            .subscribe((res: ResponseWrapper) => { this.places = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
        this.stepService.query()
            .subscribe((res: ResponseWrapper) => { this.steps = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.picture.id !== undefined) {
            this.subscribeToSaveResponse(
                this.pictureService.update(this.picture));
        } else {
            this.subscribeToSaveResponse(
                this.pictureService.create(this.picture));
        }
    }

    private subscribeToSaveResponse(result: Observable<Picture>) {
        result.subscribe((res: Picture) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Picture) {
        this.eventManager.broadcast({ name: 'pictureListModification', content: 'OK'});
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

    trackStepById(index: number, item: Step) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-picture-popup',
    template: ''
})
export class PicturePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private picturePopupService: PicturePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.picturePopupService
                    .open(PictureDialogComponent, params['id']);
            } else {
                this.modalRef = this.picturePopupService
                    .open(PictureDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
