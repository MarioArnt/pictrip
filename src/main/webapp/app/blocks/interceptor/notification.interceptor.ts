import { JhiHttpInterceptor } from 'ng-jhipster';
import { RequestOptionsArgs, Response } from '@angular/http';
import { Injector } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { PictripAlertUtils } from '../../utils/alert.utils';

export class NotificationInterceptor extends JhiHttpInterceptor {

    private alertUtils: PictripAlertUtils;

    constructor(private injector: Injector) {
        super();
        setTimeout(() => this.alertUtils = injector.get(PictripAlertUtils));
    }

    requestIntercept(options?: RequestOptionsArgs): RequestOptionsArgs {
        return options;
    }

    responseIntercept(observable: Observable<Response>): Observable<Response> {
        return observable.map((response: Response) => {
            const headers = [];
            response.headers.forEach((value, name) => {
                if (name.toLowerCase().endsWith('app-alert') || name.toLowerCase().endsWith('app-params')) {
                    headers.push(name);
                }
            });
            if (headers.length > 1) {
                headers.sort();
                const alertKey = response.headers.get(headers[ 0 ]);
                console.log('Haders', headers);
                console.log('Alertt key', alertKey);
                if (typeof alertKey === 'string') {
                    if (this.alertUtils) {
                        const alertParam = headers.length >= 2 ? response.headers.get(headers[ 1 ]) : null;
                        console.log('Alertt key', alertParam);
                        this.alertUtils.success(alertKey, { param : alertParam }, null);
                    }
                }
            }
            return response;
        }).catch((error) => {
            return Observable.throw(error); // here, response is an error
        });
    }
}
