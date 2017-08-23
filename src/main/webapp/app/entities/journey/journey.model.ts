import { BaseEntity } from './../../shared';

export const enum Transportation {
    PLANE,  TRAIN,  CAR,  TAXI,  BOAT, BUS, HITCH_HIKING, MOTORBIKE, BIKE, FOOT
}

export const TransportationColors = Object.freeze({
    PLANE: 'cyan',
    TRAIN: 'red',
    CAR: 'brown',
    TAXI: 'yellow',
    BOAT: 'blue',
    BUS: 'darkred',
    HITCH_HIKING: 'purple',
    MOTORBIKE: 'darkgreen',
    BIKE: 'green',
    FOOT: 'lightgreen'
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
