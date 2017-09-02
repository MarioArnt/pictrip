import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { MdDialog, MdDialogRef } from '@angular/material';
import { Trip } from './trip.model';
import { TripService } from './trip.service';
import { TripDeleteDialogComponent } from './trip-delete-dialog.component';

@Injectable()
export class TripPopupService {
    private loginModalRef: MdDialogRef<TripDeleteDialogComponent>;
    private isOpen = false;
    constructor(
        public dialog: MdDialog,
        private router: Router,
        private tripService: TripService
    ) {}

    open(trip: Trip): MdDialogRef<TripDeleteDialogComponent> {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;
        if (trip) {
            return this.tripModalRef(trip);
        } else {
            return this.tripModalRef(new Trip());
        }
    }

    tripModalRef(trip: Trip): MdDialogRef<TripDeleteDialogComponent> {
        this.loginModalRef = this.dialog.open(TripDeleteDialogComponent);
        this.loginModalRef.componentInstance.trip = trip;
        this.loginModalRef.afterClosed().subscribe((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return this.loginModalRef;
    }
}
