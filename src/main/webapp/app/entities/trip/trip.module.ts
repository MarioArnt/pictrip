import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PictripSharedModule } from '../../shared';
import { PictripAdminModule } from '../../admin/admin.module';
import {
    TripService,
    TripPopupService,
    TripComponent,
    TripDetailComponent,
    TripDialogComponent,
    TripPopupComponent,
    TripDeletePopupComponent,
    TripDeleteDialogComponent,
    tripRoute,
    tripPopupRoute,
} from './';
import {StepSortPipe} from '../step/step.pipes';

const ENTITY_STATES = [
    ...tripRoute,
    ...tripPopupRoute,
];

@NgModule({
    imports: [
        PictripSharedModule,
        PictripAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        TripComponent,
        TripDetailComponent,
        TripDialogComponent,
        TripDeleteDialogComponent,
        TripPopupComponent,
        TripDeletePopupComponent,
        StepSortPipe,
    ],
    entryComponents: [
        TripComponent,
        TripDialogComponent,
        TripPopupComponent,
        TripDeleteDialogComponent,
        TripDeletePopupComponent,
    ],
    providers: [
        TripService,
        TripPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PictripTripModule {}
