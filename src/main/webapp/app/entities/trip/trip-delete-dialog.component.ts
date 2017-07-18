import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Trip } from './trip.model';
import { TripPopupService } from './trip-popup.service';
import { TripService } from './trip.service';

@Component({
    selector: 'jhi-trip-delete-dialog',
    templateUrl: './trip-delete-dialog.component.html'
})
export class TripDeleteDialogComponent {

    trip: Trip;

    constructor(
        private tripService: TripService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.tripService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'tripListModification',
                content: 'Deleted an trip'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-trip-delete-popup',
    template: ''
})
export class TripDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private tripPopupService: TripPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.tripPopupService
                .open(TripDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
