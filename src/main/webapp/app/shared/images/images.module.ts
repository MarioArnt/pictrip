import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MdProgressSpinnerModule } from '@angular/material';

import {
    JusitifedGalleryComponent,
    PictureUploaderComponent
} from './';

@NgModule({
    imports: [
        BrowserModule,
        MdProgressSpinnerModule,
    ],
    declarations: [
        JusitifedGalleryComponent,
        PictureUploaderComponent,
    ],
    providers: [
    ],
    exports: [
        JusitifedGalleryComponent,
        PictureUploaderComponent,
    ]
})
export class PictripImagesModule {}
