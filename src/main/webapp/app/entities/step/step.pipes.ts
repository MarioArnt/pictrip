import { Pipe } from '@angular/core';
import {Step} from './step.model';

@Pipe({
    name: 'sortStepByNumber'
})
export class StepSortPipe {
    transform(array: Array<Step>, args: string): Array<Step> {
        array.sort((a: Step, b: Step) => {
            if (a.number < b.number) {
                return -1;
            } else if (a.number > b.number) {
                return 1;
            } else {
                return 0;
            }
        });
        return array;
    }
}
