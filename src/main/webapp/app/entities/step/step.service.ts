import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { PictripDateUtils } from '../../utils/date.utils';

import { Step } from './step.model';

import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class StepService {

    private resourceUrl = 'api/steps';
    private resourceSearchUrl = 'api/_search/steps';

    constructor(private http: Http, private dateUtils: PictripDateUtils) { }

    create(step: Step): Observable<Step> {
        const copy = this.convert(step);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(step: Step): Observable<Step> {
        const copy = this.convert(step);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: number): Observable<Step> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    findByTripId(tripId: number): Observable<ResponseWrapper> {
        return this.http.get(`api/trip/${tripId}/steps`)
            .map((res: Response) => this.convertResponse(res));
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        console.log('Send request to delete step');
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
            .map((res: any) => this.convertResponse(res));
    }

    count(tripId: number): Observable<Response> {
        return this.http.get(`api/trip/${tripId}/steps/count`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convertItemFromServer(entity: any) {
        entity.dateFrom = this.dateUtils.formatLocalDateFromServer(entity.dateFrom);
        entity.dateTo = this.dateUtils.formatLocalDateFromServer(entity.dateTo);
    }

    private convert(step: Step): any {
        const stepCopy: Step = Object.assign({}, step);
        stepCopy.dateFrom = this.dateUtils.formatLocalDateToServer(stepCopy.dateFrom);
        stepCopy.dateTo = this.dateUtils.formatLocalDateToServer(stepCopy.dateTo);
        return stepCopy;
    }
}
