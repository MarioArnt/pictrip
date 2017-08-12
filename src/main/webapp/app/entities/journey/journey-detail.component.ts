import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Journey } from './journey.model';
import { JourneyService } from './journey.service';

@Component({
    selector: 'jhi-journey-detail',
    templateUrl: './journey-detail.component.html'
})
export class JourneyDetailComponent implements OnInit, OnDestroy {

    journey: Journey;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private journeyService: JourneyService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInJourneys();
    }

    load(id) {
        this.journeyService.find(id).subscribe((journey) => {
            this.journey = journey;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInJourneys() {
        this.eventSubscriber = this.eventManager.subscribe(
            'journeyListModification',
            (response) => this.load(this.journey.id)
        );
    }
}
