import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PictripSharedModule } from '../../shared';
import {
    PictureService,
    PicturePopupService,
    PictureComponent,
    PictureDetailComponent,
    PictureDialogComponent,
    PicturePopupComponent,
    PictureDeletePopupComponent,
    PictureDeleteDialogComponent,
    pictureRoute,
    picturePopupRoute,
} from './';
import { PictureUploaderComponent } from "../../shared/images/picture-uploader/picture-uploader.component";

const ENTITY_STATES = [
    ...pictureRoute,
    ...picturePopupRoute,
];

@NgModule({
    imports: [
        PictripSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    exports: [
    ],
    declarations: [
        PictureComponent,
        PictureDetailComponent,
        PictureDialogComponent,
        PictureDeleteDialogComponent,
        PicturePopupComponent,
        PictureDeletePopupComponent,
    ],
    entryComponents: [
        PictureComponent,
        PictureDialogComponent,
        PicturePopupComponent,
        PictureDeleteDialogComponent,
        PictureDeletePopupComponent,
        //PictureUploaderComponent,
    ],
    providers: [
        PictureService,
        PicturePopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PictripPictureModule {}
