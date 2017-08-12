import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PictripTripModule } from './trip/trip.module';
import { PictripStepModule } from './step/step.module';
import { PictripPlaceModule } from './place/place.module';
import { PictripPictureModule } from './picture/picture.module';
import { PictripJourneyModule } from './journey/journey.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        PictripTripModule,
        PictripStepModule,
        PictripPlaceModule,
        PictripPictureModule,
        PictripJourneyModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PictripEntityModule {}
