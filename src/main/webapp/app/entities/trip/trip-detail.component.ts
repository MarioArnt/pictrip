import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { ResponseWrapper } from '../../shared';

import { Trip } from './trip.model';
import { TripService } from './trip.service';

import { Step } from '../step/step.model';
import { StepService } from '../step/step.service';

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
    public bounds: google.maps.LatLngBounds;
    contributors: any[];
    steps: Step[];
    lat = 43.604652;
    lng = 1.444209;
    zoom = 2;

    constructor(
        private eventManager: JhiEventManager,
        private tripService: TripService,
        private stepService: StepService,
        private route: ActivatedRoute
    ) {
        this.contributors = [];
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
        this.stepService.findByTripId(id).subscribe(
            (res: ResponseWrapper) => {
                this.steps = res.json;
                console.log(this.calculateBounds().toString());
                this.bounds = this.calculateBounds();
                console.log('Success:', this.steps);
            },
            (res: ResponseWrapper) => console.log('Error:', res.json)
        );
    }
    previousState() {
        window.history.back();
    }

    private calculateBounds() {
        const mostSouthern: number = Math.min(...this.steps.map((step) => step.placeLat));
        const mostNorthern: number = Math.max(...this.steps.map((step) => step.placeLat));
        const mostEaster: number = Math.max(...this.steps.map((step) => step.placeLng));
        const mostWestern: number = Math.min(...this.steps.map((step) => step.placeLng));
        const southWest = {
            lat: mostSouthern + 0.001,
            lng: mostWestern + 0.001,
        }
        const northEast = {
            lat: mostNorthern + 0.001,
            lng: mostEaster + 0.001,
        }
        return new google.maps.LatLngBounds(southWest, northEast);
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
