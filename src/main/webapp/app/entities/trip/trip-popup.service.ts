import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Trip } from './trip.model';
import { TripService } from './trip.service';

@Injectable()
export class TripPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private tripService: TripService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.tripService.find(id).subscribe((trip) => {
                if (trip.dateFrom) {
                    trip.dateFrom = {
                        year: trip.dateFrom.getFullYear(),
                        month: trip.dateFrom.getMonth() + 1,
                        day: trip.dateFrom.getDate()
                    };
                }
                if (trip.dateTo) {
                    trip.dateTo = {
                        year: trip.dateTo.getFullYear(),
                        month: trip.dateTo.getMonth() + 1,
                        day: trip.dateTo.getDate()
                    };
                }
                this.tripModalRef(component, trip);
            });
        } else {
            return this.tripModalRef(component, new Trip());
        }
    }

    tripModalRef(component: Component, trip: Trip): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.trip = trip;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
