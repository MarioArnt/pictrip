import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
    MdDialogModule,
    MdMenuModule,
    MdCheckboxModule,
    MdListModule,
    MdButtonModule,
    MdCardModule,
    MdTooltipModule,
    MdSidenavModule,
    MdInputModule,
    MdSliderModule,
    MdIconModule,
    MdNativeDateModule,
    MdDatepickerModule
} from '@angular/material';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgJhipsterModule } from 'ng-jhipster';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieModule } from 'ngx-cookie';
import { AgmCoreModule } from '@agm/core';
import { PictripUtilsModule } from '../utils/utils.module'
import { ToastyModule } from 'ng2-toasty';
import { Ng2FileInputModule } from 'ng2-file-input';
import { Ng2ImgToolsModule } from 'ng2-img-tools';

@NgModule({
    exports: [
        MdDialogModule,
        MdMenuModule,
        MdCheckboxModule,
        MdTooltipModule,
        MdInputModule,
        MdSliderModule,
        MdNativeDateModule,
        MdSidenavModule,
        MdIconModule,
        MdListModule,
        MdDatepickerModule,
        MdCardModule,
        MdButtonModule,
    ]
})
export class MaterialDesignModule {}

@NgModule({
    imports: [
        NgbModule.forRoot(),
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: true,
            i18nEnabled: true,
            defaultI18nLang: 'en'
        }),
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyD3P5eSwSKPg79q1gDRofEtv4aQYDvqcT0',
            libraries: ['places']
        }),
        BrowserModule,
        InfiniteScrollModule,
        Ng2ImgToolsModule,
        CookieModule.forRoot(),
        ToastyModule.forRoot(),
        Ng2FileInputModule.forRoot(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        PictripUtilsModule,
    ],
    exports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        CommonModule,
        NgbModule,
        AgmCoreModule,
        NgJhipsterModule,
        InfiniteScrollModule,
        ToastyModule,
        Ng2ImgToolsModule,
        Ng2FileInputModule,
        MaterialDesignModule,
        BrowserAnimationsModule,
        PictripUtilsModule,
    ]
})
export class PictripSharedLibsModule {}
