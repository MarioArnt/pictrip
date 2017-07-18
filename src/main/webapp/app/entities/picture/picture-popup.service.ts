import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Picture } from './picture.model';
import { PictureService } from './picture.service';

@Injectable()
export class PicturePopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private pictureService: PictureService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.pictureService.find(id).subscribe((picture) => {
                this.pictureModalRef(component, picture);
            });
        } else {
            return this.pictureModalRef(component, new Picture());
        }
    }

    pictureModalRef(component: Component, picture: Picture): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.picture = picture;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
