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
    TripDeleteDialogComponent,
    tripRoute,
} from './';
import { StepSortPipe } from '../step/step.pipes';

const ENTITY_STATES = [
    ...tripRoute,
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
        StepSortPipe,
    ],
    entryComponents: [
        TripComponent,
        TripDialogComponent,
        TripDeleteDialogComponent,
    ],
    providers: [
        TripService,
        TripPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PictripTripModule {}
