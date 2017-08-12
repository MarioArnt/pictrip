import { BaseEntity } from './../../shared';

const enum Transportation {
    'PLANE',
    ' TRAIN',
    ' CAR',
    ' TAXI',
    ' BOAT',
    ' BIKE',
    ' FOOT'
}

export class Journey implements BaseEntity {
    constructor(
        public id?: number,
        public transportation?: Transportation,
        public duration?: number,
    ) {
    }
}
