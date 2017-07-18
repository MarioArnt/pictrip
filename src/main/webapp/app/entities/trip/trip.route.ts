import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { TripComponent } from './trip.component';
import { TripDetailComponent } from './trip-detail.component';
import { TripPopupComponent } from './trip-dialog.component';
import { TripDeletePopupComponent } from './trip-delete-dialog.component';

import { Principal } from '../../shared';

export const tripRoute: Routes = [
    {
        path: 'trip',
        component: TripComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'trip/:id',
        component: TripDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const tripPopupRoute: Routes = [
    {
        path: 'trip-new',
        component: TripPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'trip/:id/edit',
        component: TripPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'trip/:id/delete',
        component: TripDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
