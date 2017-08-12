import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Journey } from './journey.model';
import { JourneyPopupService } from './journey-popup.service';
import { JourneyService } from './journey.service';

@Component({
    selector: 'jhi-journey-delete-dialog',
    templateUrl: './journey-delete-dialog.component.html'
})
export class JourneyDeleteDialogComponent {

    journey: Journey;

    constructor(
        private journeyService: JourneyService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.journeyService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'journeyListModification',
                content: 'Deleted an journey'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-journey-delete-popup',
    template: ''
})
export class JourneyDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private journeyPopupService: JourneyPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.journeyPopupService
                .open(JourneyDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
