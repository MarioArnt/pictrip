import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PictripSharedModule } from '../../shared';
import {
    JourneyService,
    JourneyPopupService,
    JourneyComponent,
    JourneyDetailComponent,
    JourneyDialogComponent,
    JourneyPopupComponent,
    JourneyDeletePopupComponent,
    JourneyDeleteDialogComponent,
    journeyRoute,
    journeyPopupRoute,
} from './';

const ENTITY_STATES = [
    ...journeyRoute,
    ...journeyPopupRoute,
];

@NgModule({
    imports: [
        PictripSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        JourneyComponent,
        JourneyDetailComponent,
        JourneyDialogComponent,
        JourneyDeleteDialogComponent,
        JourneyPopupComponent,
        JourneyDeletePopupComponent,
    ],
    entryComponents: [
        JourneyComponent,
        JourneyDialogComponent,
        JourneyPopupComponent,
        JourneyDeleteDialogComponent,
        JourneyDeletePopupComponent,
    ],
    providers: [
        JourneyService,
        JourneyPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PictripJourneyModule {}
