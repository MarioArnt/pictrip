import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager} from 'ng-jhipster';

import { Trip } from './trip.model';
import { TripService } from './trip.service';
import { Principal, ResponseWrapper } from '../../shared';
import { PictripDateUtils } from '../../utils/date.utils';
import { PictripAlertUtils } from '../../utils/alert.utils';
import { TripPopupService } from './trip-popup.service';

@Component({
    selector: 'jhi-trip',
    templateUrl: './trip.component.html',
    styleUrls: [
        'trip.scss'
    ]
})
export class TripComponent implements OnInit, OnDestroy {
trips: Trip[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        private tripService: TripService,
        private tripDeleteDialogService: TripPopupService,
        private eventManager: JhiEventManager,
        private dateUtils: PictripDateUtils,
        private activatedRoute: ActivatedRoute,
        private principal: Principal,
        private alertUtils: PictripAlertUtils,
    ) {
        this.currentSearch = activatedRoute.snapshot.params['search'] ? activatedRoute.snapshot.params['search'] : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.tripService.search({
                query: this.currentSearch,
                }).subscribe(
                    (res: ResponseWrapper) => this.trips = res.json,
                    (res: ResponseWrapper) => this.onError(res.json)
                );
            return;
       }
        this.tripService.query().subscribe(
            (res: ResponseWrapper) => {
                this.trips = res.json;
                this.currentSearch = '';
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInTrips();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    public showDeleteModal(trip: Trip) {
        this.tripDeleteDialogService.open(trip);
    }

    public summarizeText(text: string): string {
        if (text == null) {
            return '';
        }
        return text.length > 255 ? text.substr(0, 255) + '...' : text;
    }

    public formatDate(date: Date): string {
        return this.dateUtils.formatDateToHumans(date);
    }

    trackId(index: number, item: Trip) {
        return item.id;
    }
    registerChangeInTrips() {
        this.eventSubscriber = this.eventManager.subscribe('tripListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertUtils.error(error.message);
    }
}
