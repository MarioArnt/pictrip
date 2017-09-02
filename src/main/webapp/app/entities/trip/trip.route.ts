import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';

import { TripComponent } from './trip.component';
import { TripDetailComponent } from './trip-detail.component';
import { TripDialogComponent } from './trip-dialog.component';

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
    },
    {
        path: 'trip-new',
        component: TripDialogComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'trip/:id/edit',
        component: TripDialogComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.trip.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
