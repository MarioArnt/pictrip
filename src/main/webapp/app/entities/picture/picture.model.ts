import { BaseEntity } from './../../shared';

export class Picture implements BaseEntity {
    constructor(
        public id?: number,
        public src?: string,
        public caption?: string,
        public size?: number,
        public views?: number,
        public placeId?: number,
        public stepId?: number,
    ) {
    }
}
