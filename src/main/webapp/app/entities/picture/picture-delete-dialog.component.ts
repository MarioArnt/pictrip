import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Picture } from './picture.model';
import { PicturePopupService } from './picture-popup.service';
import { PictureService } from './picture.service';

@Component({
    selector: 'jhi-picture-delete-dialog',
    templateUrl: './picture-delete-dialog.component.html'
})
export class PictureDeleteDialogComponent {

    picture: Picture;

    constructor(
        private pictureService: PictureService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.pictureService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'pictureListModification',
                content: 'Deleted an picture'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-picture-delete-popup',
    template: ''
})
export class PictureDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private picturePopupService: PicturePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.picturePopupService
                .open(PictureDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
