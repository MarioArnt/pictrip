import { NgModule } from '@angular/core';

import { PictripDateUtils } from './date.utils';
import {PictripAlertUtils} from './alert.utils';

@NgModule({
    providers: [
        PictripDateUtils,
        PictripAlertUtils
    ],
})
export class PictripUtilsModule {}
