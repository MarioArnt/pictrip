import * as moment from 'moment';
import { Injectable } from '@angular/core';

@Injectable()
export class PictripDateUtils {
    private pattern: string;
    constructor() {
        this.pattern = 'YYYY-MM-DD';
    }

    public formatLocalDateToServer(date: any): string {
        return (date != null && moment(date).isValid()) ? moment(date).format(this.pattern) : null;
    }

    public formatLocalDateFromServer(dateIsoString: string): any {
        if (dateIsoString == null) {
            return null;
        }
        const date = moment(dateIsoString, this.pattern);
        return date.isValid() ? date : null;
    }

    public formatDateToHumans(date: Date): string {
        return moment(date).format('DD/MM/YYYY');
    }

}
