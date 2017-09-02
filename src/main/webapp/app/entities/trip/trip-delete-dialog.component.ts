import { Component, OnInit, OnDestroy, Injector } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { MdDialog, MdDialogRef } from '@angular/material';
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
        public activeModal: MdDialog,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.closeAll();
    }

    confirmDelete(id: number) {
        this.tripService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'tripListModification',
                content: 'Deleted an trip'
            });
            this.activeModal.closeAll();
        });
    }
}

@Component({
    selector: 'jhi-trip-delete-popup',
    template: ''
})
export class TripDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: MdDialogRef<TripDeleteDialogComponent>;
    routeSub: any;
    tripPopupService: TripPopupService;

    constructor(
        private route: ActivatedRoute,
        private injector: Injector
    ) {
        setTimeout(() => this.tripPopupService = injector.get(TripPopupService));
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.tripPopupService.open(params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
