import 'materialize-css';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
    MdDialogModule,
    MdMenuModule,
    MdCheckboxModule,
    MdButtonModule,
    MdCardModule,
    MdTooltipModule,
    MdInputModule,
    MdIconModule,
    MdNativeDateModule,
    MdDatepickerModule
} from '@angular/material';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgJhipsterModule } from 'ng-jhipster';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieModule } from 'ngx-cookie';
import { MaterializeModule } from 'angular2-materialize';

@NgModule({
    exports: [
        MdDialogModule,
        MdMenuModule,
        MdCheckboxModule,
        MdTooltipModule,
        MdInputModule,
        MdNativeDateModule,
        MdIconModule,
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
        InfiniteScrollModule,
        CookieModule.forRoot(),
        MaterializeModule,
        MaterialDesignModule,
        BrowserAnimationsModule,
    ],
    exports: [
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        CommonModule,
        NgbModule,
        NgJhipsterModule,
        InfiniteScrollModule,
        MaterializeModule,
        MaterialDesignModule,
        BrowserAnimationsModule
    ]
})
export class PictripSharedLibsModule {}
