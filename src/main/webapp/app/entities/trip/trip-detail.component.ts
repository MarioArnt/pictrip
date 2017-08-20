import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { ResponseWrapper } from '../../shared';

import { Trip } from './trip.model';
import { TripService } from './trip.service';

import { Step } from '../step/step.model';
import { StepService } from '../step/step.service';
import { Journey, TransportationColors } from '../journey/journey.model';
import { JourneyService } from '../journey/journey.service';

@Component({
    selector: 'jhi-trip-detail',
    templateUrl: './trip-detail.component.html',
    styleUrls: [
        'trip.scss'
    ]
})
export class TripDetailComponent implements OnInit, OnDestroy {

    public trip: Trip;
    public bounds: google.maps.LatLngBounds;
    public contributors: any[];
    public journeys: Journey[];
    public steps: Step[];
    public lat: number;
    public lng: number;
    public zoom: number;

    private subscription: Subscription;
    private eventSubscriber: Subscription;
    public transportationColors: any;

    constructor(
        private eventManager: JhiEventManager,
        private tripService: TripService,
        private stepService: StepService,
        private journeyService: JourneyService,
        private route: ActivatedRoute,
    ) {
        this.steps = [];
        this.contributors = [];
        this.journeys = [];
        this.lat = 43.604652;
        this.transportationColors = TransportationColors;
        this.lng = 1.444209;
        this.zoom = 2;
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInTrips();
        this.eventManager.subscribe('stepListModification', () => {
            this.fetchSteps(this.trip.id);
        });
        this.journeyService.query().subscribe(
            (res: ResponseWrapper) => {
                this.journeys = res.json;
            },
            (res: ResponseWrapper) => console.log('Error fetching journeys')
        );
    }

    load(id) {
        this.tripService.find(id).subscribe((trip) => {
            this.trip = trip;
        });
        this.fetchSteps(id);
    }

    private fetchSteps(tripId) {
        this.steps = [];
        this.stepService.findByTripId(tripId).subscribe(
            (res: ResponseWrapper) => {
                this.steps = res.json;
                if (this.steps.length > 1) {
                    this.bounds = this.calculateBounds();
                } else if (this.steps.length === 1) {
                    this.lat = this.steps[0].placeLat;
                    this.lng = this.steps[0].placeLng;
                    this.zoom = 12;
                }
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
            lat: mostSouthern,
            lng: mostWestern,
        }
        const northEast = {
            lat: mostNorthern,
            lng: mostEaster,
        }
        return new google.maps.LatLngBounds(southWest, northEast);
    }

    zoomOnStep(step: Step) {
        this.lat = step.placeLat;
        this.lng = step.placeLng;
        this.zoom = 12;
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
