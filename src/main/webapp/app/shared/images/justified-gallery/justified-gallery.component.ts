import { Component, Input, DoCheck, IterableDiffer, IterableDiffers, KeyValueDiffers } from '@angular/core';
import * as justifiedLayout from 'justified-layout';
import {Subject} from "rxjs";

@Component({
    selector: 'ng2-justified-gallery',
    templateUrl: './justified-gallery.component.html',
    styleUrls: [
        'justified-gallery.scss'
    ]
})
export class JusitifedGalleryComponent implements DoCheck {
    @Input() pictures:any[];
    @Input() targetRowHeight: number;
    @Input() boxSpacing: number;
    @Input() targetRowHeightTolerance: number;
    @Input() containerPadding: number;
    @Input() maxNumRows: number;
    @Input() forceAspectRatio: boolean;
    @Input() showWidows: boolean;
    @Input() fullWidthBreakoutRowCadence: boolean;
    private differ: IterableDiffer<any>;
    public boxes: any[];
    private subject: Subject<any> = new Subject();
    private debounced: boolean = false;

    constructor(
        private differs: IterableDiffers,
    ) {
        this.differ = differs.find([]).create(null);
        this.pictures = [];
        this.boxes = [];
        this.targetRowHeight = 200;
        this.targetRowHeightTolerance = 0.25;
        this.boxSpacing = 3;
        this.maxNumRows = Number.POSITIVE_INFINITY;
        this.forceAspectRatio = false;
        this.showWidows = true;
        this.fullWidthBreakoutRowCadence= false;
        this.containerPadding = 0;
    }

    ngOnInit() {
        this.calculateImagesDimension();
        this.subject.debounceTime(50).subscribe(
            () => this.debounce()
        );
    }


    ngDoCheck() {
        const changes = this.differ.diff(this.pictures);

        if(changes) {
            if (!this.debounced) {
                this.subject.next();
            } else {
                this.debounced = false;
            }
        }
    }

    calculateImagesDimension(): any {

        console.log('Box updating...', Date.now());
        const aspectRatios = this.pictures.map((picture) => picture.width/picture.height);
        const containerWidth = document.getElementById('justified-gallery-container').clientWidth;
        const geometry = justifiedLayout(aspectRatios, {
            containerWidth: containerWidth,
            containerPadding: this.containerPadding,
            boxSpacing: this.boxSpacing,
            targetRowHeight: this.targetRowHeight,
            targetRowHeightTolerance: this.targetRowHeightTolerance,
            maxNumRows: this.maxNumRows,
            forceAspectRatio: this.forceAspectRatio,
            showWidows: this.showWidows,
            fullWidthBreakoutRowCadence: this.fullWidthBreakoutRowCadence
        });
        this.boxes = geometry.boxes;
        console.log('Box updated !', Date.now());
        return geometry;
    }

    debounce() {
        this.debounced = true;
        this.calculateImagesDimension();
    }
}
