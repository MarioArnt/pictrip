import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Picture } from './picture.model';
import { PictureService } from './picture.service';

@Component({
    selector: 'jhi-picture-detail',
    templateUrl: './picture-detail.component.html'
})
export class PictureDetailComponent implements OnInit, OnDestroy {

    picture: Picture;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private pictureService: PictureService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPictures();
    }

    load(id) {
        this.pictureService.find(id).subscribe((picture) => {
            this.picture = picture;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPictures() {
        this.eventSubscriber = this.eventManager.subscribe(
            'pictureListModification',
            (response) => this.load(this.picture.id)
        );
    }
}
