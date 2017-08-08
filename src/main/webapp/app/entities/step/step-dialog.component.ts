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

@Component({
    selector: 'jhi-step-dialog',
    templateUrl: './step-dialog.component.html',
    styleUrls: [
        'step.scss'
    ]
})
export class StepDialogComponent implements OnInit {

    step: Step;
    authorities: any[];
    isSaving: boolean;
    routeSub: any;
    lat: number;
    lng: number;
    zoom: number;
    addressResolved: boolean;
    stepNumber: number;

    @ViewChild('placeAutocomplete')
    public placeAutocompleteElementRef: ElementRef;

    constructor(
        private alertService: JhiAlertService,
        private stepService: StepService,
        private route: ActivatedRoute,
        private eventManager: JhiEventManager,
        private mapsAPILoader: MapsAPILoader,
        private ngZone: NgZone,
    ) {
        this.lat = 43.604652;
        this.lng = 1.444209;
        this.zoom = 2;
        this.stepNumber = 1;
        this.step = new Step();
        this.routeSub = this.route.params.subscribe((params) => {
            const id = params['id'];
            if (id) {
                this.stepService.find(id).subscribe((step) => {
                    if (step.dateFrom) {
                        step.dateFrom = {
                            year: step.dateFrom.getFullYear(),
                            month: step.dateFrom.getMonth() + 1,
                            day: step.dateFrom.getDate()
                        };
                    }
                    if (step.dateTo) {
                        step.dateTo = {
                            year: step.dateTo.getFullYear(),
                            month: step.dateTo.getMonth() + 1,
                            day: step.dateTo.getDate()
                        };
                    }
                    this.step = step;
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
                    console.log('Place correct');
                    console.log(this.lat, this.lng);
                    // set latitude, longitude and zoom
                    this.lat = place.geometry.location.lat();
                    this.lng = place.geometry.location.lng();
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
                this.stepService.update(this.step));
        } else {
            this.subscribeToSaveResponse(
                this.stepService.create(this.step));
        }
    }

    private subscribeToSaveResponse(result: Observable<Step>) {
        result.subscribe((res: Step) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Step) {
        this.eventManager.broadcast({ name: 'stepListModification', content: 'OK'});
        this.isSaving = false;
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
