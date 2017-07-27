import 'materialize-css';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MdDialogModule, MdMenuModule, MdTooltipModule, MdInputModule } from '@angular/material';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgJhipsterModule } from 'ng-jhipster';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieModule } from 'ngx-cookie';
import { MaterializeModule } from 'angular2-materialize';

@NgModule({
    exports: [
        MdDialogModule,
        MdMenuModule,
        MdTooltipModule,
        MdInputModule,
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
