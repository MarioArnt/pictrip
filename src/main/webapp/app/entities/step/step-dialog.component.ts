import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Step } from './step.model';
import { StepPopupService } from './step-popup.service';
import { StepService } from './step.service';
import { Place, PlaceService } from '../place';
import { Trip, TripService } from '../trip';
import { ResponseWrapper } from '../../shared';
import { ElementRef, NgZone, ViewChild } from '@angular/core';
import { MapsAPILoader } from '@agm/core';
import {} from '@types/googlemaps';
import { Router } from '@angular/router';
@Component({
    selector: 'jhi-step-dialog',
    templateUrl: './step-dialog.component.html',
    styleUrls: [
        'step.scss'
    ]
})
export class StepDialogComponent implements OnInit {

    step: Step;
    place: Place;
    authorities: any[];
    isSaving: boolean;
    routeSub: any;
    zoom: number;
    addressResolved: boolean;
    stepNumber: number;

    @ViewChild('placeAutocomplete')
    public placeAutocompleteElementRef: ElementRef;

    constructor(
        private alertService: JhiAlertService,
        private stepService: StepService,
        private placeService: PlaceService,
        private route: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private mapsAPILoader: MapsAPILoader,
        private ngZone: NgZone,
    ) {
        this.place = new Place();
        this.place.lat = 43.604652;
        this.place.lon = 1.444209;
        this.zoom = 2;
        this.stepNumber = 1;
        this.step = new Step();

        this.routeSub = this.route.params.subscribe((params) => {
            this.step.tripId = params['tripId'];
            const id = params['id'];
            if (id) {
                this.stepService.find(id).subscribe((step) => {
                    this.step = step;
                    this.place = new Place();
                    this.place.name = this.step.placeName;
                    this.place.lat = this.step.placeLat;
                    this.place.lon = this.step.placeLng;
                    this.zoom = 12;
                    this.addressResolved = true;
                });
            }
        })
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];

        // load Places Autocomplete
        this.mapsAPILoader.load().then(() => {
            const autocomplete = new google.maps.places.Autocomplete(this.placeAutocompleteElementRef.nativeElement, {
                types: []
            });
            autocomplete.addListener('place_changed', () => {
                console.log('Place changed');
                this.addressResolved = false;
                this.ngZone.run(() => {
                    // get the place result
                    const place: google.maps.places.PlaceResult = autocomplete.getPlace();

                    // verify result
                    if (place.geometry === undefined || place.geometry === null) {
                        return;
                    }
                    console.log('Place correct', place);
                    console.log(this.place.lat, this.place.lon);
                    // set latitude, longitude and zoom
                    this.place.lat = place.geometry.location.lat();
                    this.place.lon = place.geometry.location.lng();
                    this.place.name = place.formatted_address;
                    this.zoom = 12;
                    this.addressResolved = true;
                });
            });
        });
    }

    clear() {
    }

    save() {
        this.isSaving = true;
        if (this.step.id !== undefined) {
            this.subscribeToSaveResponse(
                this.stepService.update(this.step, this.place));
        } else {
            this.subscribeToSaveResponse(
                this.stepService.create(this.step, this.place));
        }
    }

    placeChanged() {
        this.addressResolved = false;
    }

    private subscribeToSaveResponse(result: Observable<Step>) {
        result.subscribe((res: Step) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Step) {
        this.eventManager.broadcast({ name: 'stepListModification', content: 'OK'});
        this.isSaving = false;
        this.router.navigate(['/trip/' + this.step.tripId]);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-step-popup',
    template: ''
})
export class StepPopupComponent implements OnInit, OnDestroy {
    constructor(
        private stepPopupService: StepPopupService
    ) {}

    ngOnInit() {
        /*;*/
    }

    ngOnDestroy() {
    }
}
