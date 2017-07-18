import { BaseEntity, User } from './../../shared';

const enum Privacy {
    'PUBLIC',
    ' ANYONE_WITH_LINK',
    ' PRIVATE'
}

const enum Color {
    'RED',
    'PINK',
    'PURPLE',
    'DEEP_PURPLE',
    'INDIGO',
    'BLUE',
    'LIGHT_BLUE',
    'CYAN',
    'TEAL',
    'GREEN',
    'LIGHT_GREEN',
    'LIME',
    'YELLOW',
    'AMBER',
    'ORANGE',
    'DEEP_ORANGE',
    'BROWN'
}

export class Trip implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public dateFrom?: any,
        public dateTo?: any,
        public privacy?: Privacy,
        public color?: Color,
        public coverId?: number,
        public steps?: BaseEntity[],
        public members?: User[],
        public ownerId?: number,
    ) {
    }
}
