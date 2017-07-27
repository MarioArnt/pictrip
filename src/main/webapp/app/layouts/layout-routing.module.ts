import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { navbarRoute } from '../app.route';
import { errorRoute } from './';
import { MaterialDesignModule } from '../shared/shared-libs.module';

const LAYOUT_ROUTES = [
    navbarRoute,
    ...errorRoute
];

@NgModule({
    imports: [
        RouterModule.forRoot(LAYOUT_ROUTES, { useHash: true }),
        MaterialDesignModule,
    ],
    exports: [
        RouterModule
    ]
})
export class LayoutRoutingModule {}
