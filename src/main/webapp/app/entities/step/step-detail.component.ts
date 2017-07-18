import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Step } from './step.model';
import { StepService } from './step.service';

@Component({
    selector: 'jhi-step-detail',
    templateUrl: './step-detail.component.html'
})
export class StepDetailComponent implements OnInit, OnDestroy {

    step: Step;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private stepService: StepService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSteps();
    }

    load(id) {
        this.stepService.find(id).subscribe((step) => {
            this.step = step;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSteps() {
        this.eventSubscriber = this.eventManager.subscribe(
            'stepListModification',
            (response) => this.load(this.step.id)
        );
    }
}
