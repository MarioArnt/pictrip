import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { Picture } from './picture.model';
import { PictureService } from './picture.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-picture',
    templateUrl: './picture.component.html'
})
export class PictureComponent implements OnInit, OnDestroy {
pictures: Picture[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        private pictureService: PictureService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
    ) {
        this.currentSearch = activatedRoute.snapshot.params['search'] ? activatedRoute.snapshot.params['search'] : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.pictureService.search({
                query: this.currentSearch,
                }).subscribe(
                    (res: ResponseWrapper) => this.pictures = res.json,
                    (res: ResponseWrapper) => this.onError(res.json)
                );
            return;
       }
        this.pictureService.query().subscribe(
            (res: ResponseWrapper) => {
                this.pictures = res.json;
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
        this.registerChangeInPictures();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Picture) {
        return item.id;
    }
    registerChangeInPictures() {
        this.eventSubscriber = this.eventManager.subscribe('pictureListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
