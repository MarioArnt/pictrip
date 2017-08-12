import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Journey } from './journey.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class JourneyService {

    private resourceUrl = 'api/journeys';
    private resourceSearchUrl = 'api/_search/journeys';

    constructor(private http: Http) { }

    create(journey: Journey): Observable<Journey> {
        const copy = this.convert(journey);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(journey: Journey): Observable<Journey> {
        const copy = this.convert(journey);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Journey> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
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
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(journey: Journey): Journey {
        const copy: Journey = Object.assign({}, journey);
        return copy;
    }
}
