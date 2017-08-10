import { BaseEntity } from './../../shared';

export class Step implements BaseEntity {
    constructor(
        public id?: number,
        public number?: number,
        public description?: string,
        public dateFrom?: any,
        public dateTo?: any,
        public placeId?: number,
        public placeName?: string,
        public placeLat?: number,
        public placeLng?: number,
        public pictures?: BaseEntity[],
        public tripId?: number,
    ) {
    }
}
