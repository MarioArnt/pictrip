import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {
    JusitifedGalleryComponent,
} from './';

@NgModule({
    imports: [
        BrowserModule,
    ],
    declarations: [
        JusitifedGalleryComponent,
    ],
    providers: [
    ],
    exports: [
        JusitifedGalleryComponent,
    ]
})
export class PictripImagesModule {}
