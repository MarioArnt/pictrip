import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Step } from './step.model';
import { StepService } from './step.service';

@Injectable()
export class StepPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private stepService: StepService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

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
                this.stepModalRef(component, step);
            });
        } else {
            return this.stepModalRef(component, new Step());
        }
    }

    stepModalRef(component: Component, step: Step): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.step = step;
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
