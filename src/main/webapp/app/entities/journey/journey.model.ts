import { BaseEntity } from './../../shared';

export const enum Transportation {
    PLANE,  TRAIN,  CAR,  TAXI,  BOAT, BUS, HITCH_HIKING, MOTORBIKE, BIKE, FOOT
}

export const TransportationColors = Object.freeze({
    PLANE: '#0277BD',
    TRAIN: '#D84315',
    CAR: '#4E342E',
    TAXI: '#F9A825',
    BOAT: '#283593',
    BUS: '#6A1B9A',
    HITCH_HIKING: '#AD1457',
    MOTORBIKE: '#9E9D24',
    BIKE: '#2E7D32',
    FOOT: '#558B2F'
});

export class Journey implements BaseEntity {
    constructor(
        public id?: number,
        public transportation?: Transportation,
        public duration?: number,
        public stepFromId?: number,
        public stepToId?: number,
    ) {
    }
}
