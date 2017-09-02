import { Injectable, ViewContainerRef } from '@angular/core';
import { ToastyService, ToastyConfig, ToastOptions, ToastData } from 'ng2-toasty';
import { JhiConfigService } from 'ng-jhipster/src/config.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class PictripAlertUtils {

    private i18nEnabled: boolean;
    private toastOptions: ToastOptions;
    constructor(
        private configService: JhiConfigService,
        private translateService: TranslateService,
        private toasty: ToastyService,
        private toastyConfig: ToastyConfig,
    ) {
        const config = this.configService.getConfig();
        this.i18nEnabled = config.i18nEnabled;
        this.toastyConfig.theme = 'default';
        this.toastOptions = new ToastOptions();
        this.toastOptions.showClose = false;
        this.toastOptions.timeout = 2000;
        this.toastOptions.theme = 'default';
    }

    private getMessageI18n(i18nKey: string, i18nParams?: any): string {
        let msg = i18nKey;
        if (this.i18nEnabled && i18nKey && i18nParams) {
            msg = this.translateService.instant(i18nKey, i18nParams);
        }
        return msg;
    }

    public success(i18nKey: string, i18nParams?: any, title?: string, options?: any) {
        this.toastOptions.msg = this.getMessageI18n(i18nKey, i18nParams);
        this.toastOptions.title = title != null ? title : 'Success !';
        this.toasty.success(this.toastOptions);
    }

    public error(i18nKey: string, i18nParams?: any, title?: string, options?: any) {
        this.toastOptions.msg = this.getMessageI18n(i18nKey, i18nParams);
        this.toastOptions.title = title != null ? title : 'Error !';
        this.toasty.error(this.toastOptions);
    }
}
