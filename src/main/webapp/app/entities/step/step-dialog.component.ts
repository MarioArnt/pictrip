import { Component, OnInit, OnDestroy } from '@angular/core';
import { ElementRef, NgZone, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';
import { MapsAPILoader } from '@agm/core';
import {} from '@types/googlemaps';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';
import { PictripAlertUtils } from '../../utils/alert.utils';

import { Step } from './step.model';
import { StepPopupService } from './step-popup.service';
import { StepService } from './step.service';

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
    zoom: number;
    addressResolved: boolean;
    maxNumberSteps: number;
    update: boolean;
    formData: any;
    uploadInProgress: boolean;

    @ViewChild('placeAutocomplete')
    public placeAutocompleteElementRef: ElementRef;

    constructor(
        private stepService: StepService,
        private route: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private mapsAPILoader: MapsAPILoader,
        private alertUtils: PictripAlertUtils,
        private ngZone: NgZone,
    ) {
        this.zoom = 2;
        this.step = new Step();
        this.step.placeLat = 43.604652;
        this.step.placeLng = 1.444209;
        this.update = false;
        this.maxNumberSteps = 1;
        this.formData = {};
        this.uploadInProgress = false;

        this.routeSub = this.route.params.subscribe((params) => {
            this.step.tripId = params['tripId'];
            this.formData.tripId = params['tripId'];
            const id = params['id'];
            this.stepService.count(this.step.tripId).subscribe((response) => {
                const numberSteps = parseInt(response.text(), 10);
                if (id) {
                    this.stepService.find(id).subscribe((step) => {
                        this.step = step;
                        this.update = true;
                        this.zoom = 12;
                        this.addressResolved = true;
                        this.maxNumberSteps = numberSteps;
                        this.formData.stepId = this.step.id;
                    });
                } else {
                    this.maxNumberSteps = numberSteps + 1;
                    this.step.number = this.maxNumberSteps;
                }
            });
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
                    console.log(this.step.placeLat, this.step.placeLng);
                    // set latitude, longitude and zoom
                    this.step.placeLat = place.geometry.location.lat();
                    this.step.placeLng = place.geometry.location.lng();
                    this.step.placeName = place.formatted_address;
                    this.zoom = 12;
                    this.addressResolved = true;
                });
            });
        });
    }

    clear() {
    }

    changeTransportation(transportation: any) {
        this.step.arrivalTransportation = transportation;
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

    placeChanged() {
        this.addressResolved = false;
    }

    stepNumberChanged() {
        if (this.step.number > this.maxNumberSteps) {
            this.step.number = this.maxNumberSteps;
        }
    }

    filesAdded() {
        this.uploadInProgress = true;
    }

    picturesUploaded($event) {
        console.log('>>>>> IMG UPLOADED', $event);
        this.uploadInProgress = false;
        const picturesId = $event.responses.map((res) => res.json());
        this.step.pictures = picturesId;
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
        console.log('Error happened');
        this.alertUtils.error(error.message);
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
