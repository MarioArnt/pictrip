import { Component, Input, ViewChild, ElementRef, ViewChildren, QueryList } from '@angular/core';
import * as justifiedLayout from 'justified-layout';
import {DomSanitizer} from "@angular/platform-browser";

@Component({
    selector: 'jhi-image-uploader',
    templateUrl: './picture-uploader.component.html',
    styleUrls: [
        'picture.scss'
    ]
})
export class PictureUploaderComponent {

    @Input() targetRowHeight: number;
    @Input() boxSpacing: number;
    @Input() targetRowHeightTolerance: number;
    @Input() containerPadding: number;
    @Input() maxNumRows: number;
    @Input() forceAspectRatio: boolean;
    @Input() showWidows: boolean;
    @Input() fullWidthBreakoutRowCadence: boolean;

    @ViewChild('fileInput') fileInput: ElementRef;
    @ViewChildren('boxes') boxesElement: QueryList<any>;

    extensions: string[];
    pictures: any[];
    boxes: any[];
    galleryHeight: number;

    constructor(
        private sanitizer: DomSanitizer
    ) {
        this.extensions = ['jpg', 'jpeg', 'png'];
        this.pictures = [];
        this.boxes = [];
        this.targetRowHeight = 125;
        this.targetRowHeightTolerance = 0.25;
        this.boxSpacing = 3;
        this.maxNumRows = Number.POSITIVE_INFINITY;
        this.forceAspectRatio = false;
        this.showWidows = true;
        this.fullWidthBreakoutRowCadence= false;
        this.containerPadding = 0;
        this.galleryHeight = 0;
    }

    ngAfterViewInit() {
        this.boxesElement.changes.subscribe(() => {
            this.renderPreviews();
        })
    }

    selectFiles() {
        let event = new MouseEvent('click', {bubbles: true});
        this.fileInput.nativeElement.dispatchEvent(event);
    }

    handleFiles($event) {
        const files = $event.srcElement.files;
        const imageLoaded = [];
        for (let i = 0; i < files.length; i++) {
            const img = document.createElement("img");
            img.src = window.URL.createObjectURL(files[i]);
            imageLoaded.push(this.loadImage(img, files[i]));
        }
        Promise.all(imageLoaded).then(() => {
            this.calculateImagesDimension();
        });
    }

    private loadImage(img: any, file: File) {
        const id = img.src.match(/([a-zA-Z0-9-]+)$/);
        return new Promise((resolve) => {
            img.onload = () => {
                window.URL.revokeObjectURL(img.src);
                this.pictures.push({
                    id: id[0],
                    imgElement: img,
                    uploaded: false,
                    file: file,
                });
                resolve();
            }
        });
    }

    private renderPreviews() {
        const lastBox = this.boxes[this.boxes.length -1];
        console.log(lastBox.top + lastBox.height);
        this.galleryHeight = lastBox.top + lastBox.height;
        this.pictures.forEach((picture) => {
            const box = document.getElementById(picture.id);
            if(box) {
               picture.imgElement.width = box.clientWidth;
               picture.imgElement.height = box.clientHeight;
               box.appendChild(picture.imgElement);
           }
        });
    }

    private calculateImagesDimension(): any {
        const aspectRatios = this.pictures.map((picture) => picture.imgElement.width / picture.imgElement.height);
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
        return geometry;
    }
}
