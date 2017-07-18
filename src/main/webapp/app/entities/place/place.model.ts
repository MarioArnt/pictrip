import { BaseEntity } from './../../shared';

export class Place implements BaseEntity {
    constructor(
        public id?: number,
        public lat?: number,
        public lon?: number,
        public name?: string,
    ) {
    }
}
