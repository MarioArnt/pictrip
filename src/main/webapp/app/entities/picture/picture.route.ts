import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { PictureComponent } from './picture.component';
import { PictureDetailComponent } from './picture-detail.component';
import { PicturePopupComponent } from './picture-dialog.component';
import { PictureDeletePopupComponent } from './picture-delete-dialog.component';

import { Principal } from '../../shared';

export const pictureRoute: Routes = [
    {
        path: 'picture',
        component: PictureComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.picture.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'picture/:id',
        component: PictureDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.picture.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const picturePopupRoute: Routes = [
    {
        path: 'picture-new',
        component: PicturePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.picture.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'picture/:id/edit',
        component: PicturePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.picture.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'picture/:id/delete',
        component: PictureDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pictripApp.picture.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
