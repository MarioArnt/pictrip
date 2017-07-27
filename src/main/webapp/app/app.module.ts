import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';
import { PictripSharedModule, UserRouteAccessService } from './shared';
import { PictripHomeModule } from './home/home.module';
import { PictripAdminModule } from './admin/admin.module';
import { PictripAccountModule } from './account/account.module';
import { PictripEntityModule } from './entities/entity.module';

import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';
import { ToastModule, ToastOptions } from 'ng2-toastr/ng2-toastr';
import { CustomToasts } from './shared/toast/custom-toasts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PictripSharedLibsModule } from './shared/shared-libs.module';

// jhipster-needle-angular-add-module-import JHipster will add new module here

import {
    JhiMainComponent,
    LayoutRoutingModule,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent
} from './layouts';
import {LoginModalService} from './shared/login/login-modal.service';

@NgModule({
    imports: [
        BrowserModule,
        LayoutRoutingModule,
        ToastModule.forRoot(),
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        PictripSharedModule,
        PictripHomeModule,
        PictripAdminModule,
        PictripAccountModule,
        PictripEntityModule,
        PictripSharedLibsModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent,
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService,
        LoginModalService,
        {provide: ToastOptions, useClass: CustomToasts}
    ],
    bootstrap: [ JhiMainComponent ]
})
export class PictripAppModule {}
