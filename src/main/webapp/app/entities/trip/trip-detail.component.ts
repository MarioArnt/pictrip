import { Component, OnInit, OnDestroy, ViewChildren, QueryList } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { ResponseWrapper } from '../../shared';
import { PictripDateUtils } from '../../utils/date.utils';
import { AgmInfoWindow } from '@agm/core';

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
    public journeys: any[];
    public steps: Step[];
    public selectedStep: Step;
    public lat: number;
    public lng: number;
    public zoom: number;
    public showDeleteWindow: boolean;

    private subscription: Subscription;
    private eventSubscriber: Subscription;
    private journeyStrokeOpacity: number;
    private journeyHoveredStrokeOpacity: number;
    public transportationColors: any;

    @ViewChildren('stepInfoWindow')
    public stepInfoWindows: QueryList<AgmInfoWindow>;

    constructor(
        private eventManager: JhiEventManager,
        private tripService: TripService,
        private stepService: StepService,
        private journeyService: JourneyService,
        private dateUtils: PictripDateUtils,
        private route: ActivatedRoute,
    ) {
        this.steps = [];
        this.selectedStep = null;
        this.showDeleteWindow = false;
        this.contributors = [];
        this.journeys = [];
        this.lat = 43.604652;
        this.lng = 1.444209;
        this.transportationColors = TransportationColors;
        this.journeyStrokeOpacity = 0.6;
        this.journeyHoveredStrokeOpacity = 1;
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
                this.journeys.forEach((j) => j.strokeOpacity = this.journeyStrokeOpacity);
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
                this.steps = res.json.sort((a, b) => a.number > b.number);
                this.centerMapOnStep(1);
            },
            (res: ResponseWrapper) => console.log('Error:', res.json)
        );
    }

    /**
     * Select a step by number. The map will smoothly translate to new bounds, with the step in center
     * and the journey to this step visible. The step will also be highlighted in step list.
     * @param stepNumber : the number of the step to select
     */
    public selectStep(stepNumber: number): void {
        this.stepInfoWindows.forEach((infoWindow) => infoWindow.close());
        this.stepInfoWindows.find((infoWindow) => parseInt(infoWindow.hostMarker.label, 10) === stepNumber).open();
        this.centerMapOnStep(stepNumber);
    }

    private centerMapOnStep(stepNumber: number) {
        this.selectedStep = this.steps.find((s) => s.number === stepNumber);
        // Set center
        this.lat = this.selectedStep.placeLat;
        this.lng = this.selectedStep.placeLng;
    }

    public closeInfoWindow(infoWindow: AgmInfoWindow) {
        infoWindow.close();
    }

    public formatDate(date: Date): string {
        return this.dateUtils.formatDateToHumans(date);
    }

    public summarizeText(text: string): string {
        if (text == null) {
            return '';
        }
        return text.length > 255 ? text.substr(0, 255) + '...' : text;
    }

    /**
     * Display the info window to confirm step delete. Will also focus the step to delete.
     * @param step : the step to delete
     */
    public deleteStep(step: Step): void {
        this.selectStep(step.number);
        this.showDeleteWindow = true;
    }

    public confirmStepDeletion(): void {
        this.showDeleteWindow = false;
        this.stepService.delete(this.selectedStep.id);
    }

    public cancelStepDeletion(): void {
        this.showDeleteWindow = false;
    }

    previousState() {
        window.history.back();
    }

    boundsChanged($event) {
        console.log('Bounds changed:', $event);
    }

    centerChanged($event) {
        console.log('Center changed:', $event);
    }

    journeyMouseOver(journey) {
        journey.strokeOpacity = this.journeyHoveredStrokeOpacity;
    }

    journeyMouseOut(journey) {
        journey.strokeOpacity = this.journeyStrokeOpacity;
    }

    journeyClicked(journey) {
        this.journeys.forEach((j) => j.strokeOpacity = this.journeyStrokeOpacity);
        journey.strokeOpacity = this.journeyHoveredStrokeOpacity;
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
