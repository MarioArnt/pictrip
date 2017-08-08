import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils } from 'ng-jhipster';
import * as moment from 'moment';

import { Step } from './step.model';
import { Place } from '../place/place.model';

import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class StepService {

    private resourceUrl = 'api/steps';
    private resourceSearchUrl = 'api/_search/steps';

    constructor(private http: Http, private dateUtils: JhiDateUtils) { }

    create(step: Step, place: Place): Observable<Step> {
        const copy = this.convert(step, place);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(step: Step, place: Place): Observable<Step> {
        const copy = this.convert(step, place);
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

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
            .map((res: any) => this.convertResponse(res));
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convertItemFromServer(entity: any) {
        entity.dateFrom = this.dateUtils
            .convertLocalDateFromServer(entity.dateFrom);
        entity.dateTo = this.dateUtils
            .convertLocalDateFromServer(entity.dateTo);
    }

    private convert(step: Step, place: Place): any {
        const stepCopy: Step = Object.assign({}, step);
        const placeCopy: Place = Object.assign({}, place);
        stepCopy.dateFrom = moment(step.dateFrom).format('YYYY-MM-DD');
        stepCopy.dateTo = moment(step.dateTo).format('YYYY-MM-DD');
        return {
            stepDTO: stepCopy,
            placeDTO: placeCopy,
        };
    }
}
