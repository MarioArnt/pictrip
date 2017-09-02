import { Component } from '@angular/core';

import { MdDialog, MdDialogRef } from '@angular/material';
import { JhiEventManager } from 'ng-jhipster';

import { Trip } from './trip.model';
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
