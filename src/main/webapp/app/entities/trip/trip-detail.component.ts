import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Trip } from './trip.model';
import { TripService } from './trip.service';

@Component({
    selector: 'jhi-trip-detail',
    templateUrl: './trip-detail.component.html',
    styleUrls: [
        'trip.scss'
    ]
})
export class TripDetailComponent implements OnInit, OnDestroy {

    trip: Trip;
    private subscription: Subscription;
    private eventSubscriber: Subscription;
    lat = 43.604652;
    lng = 1.444209;
    zoom = 2;

    constructor(
        private eventManager: JhiEventManager,
        private tripService: TripService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInTrips();
    }

    load(id) {
        this.tripService.find(id).subscribe((trip) => {
            this.trip = trip;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInTrips() {
        this.eventSubscriber = this.eventManager.subscribe(
            'tripListModification',
            (response) => this.load(this.trip.id)
        );
    }
}
