import { Component, Input, ViewChild, ElementRef, ViewChildren, QueryList, AfterViewInit } from '@angular/core';
import * as justifiedLayout from 'justified-layout';
import { Ng2PicaService } from 'ng2-pica';
import {Observable} from "rxjs/Observable";

const enum PictureState {
    'QUEUED',
    'RESIZING',
    'UPLOADING',
    'UPLOADED',
    'ABORTED'
}

class PictureToUpload {
    id: string;
    imgElement: any;
    state: PictureState;
    originalFile: File;
    resizedFile: File;
}

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
    pictures: PictureToUpload[];
    boxes: any[];
    galleryHeight: number;
    resizeWidth: number;
    resizeHeight: number;
    loadingPictures: boolean;

    constructor(
        private ng2PicaService: Ng2PicaService
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
        this.resizeWidth = 1920;
        this.resizeHeight = 1080;
        this.loadingPictures = false;
    }

    ngAfterViewInit() {
        this.galleryHeight = 0;
        this.boxesElement.changes.subscribe(() => {
            console.log('Box rendered, displaying images');
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
        this.loadingPictures = true;
        for (let i = 0; i < files.length; i++) {
            const img = document.createElement("img");
            img.src = window.URL.createObjectURL(files[i]);
            imageLoaded.push(this.loadImage(img, files[i]));
        }
        Promise.all(imageLoaded).then(() => {
            this.loadingPictures = false;
            console.log('All images loaded !')
            this.calculateImagesDimension();
            console.log('Box dimensions updated !')
            const toResize = Observable.of(...this.pictures);
            const resizeSequence = toResize.concatMap(picture => this.resizeImage(picture));
            resizeSequence.subscribe((resizedFile) => {
                console.log('File resized', resizedFile);
                console.log('TODO Upload');
            });
        });
    }

    /*public resizeImagesAndUpload(): Observable<any> {
        const subject = Observable.empty();
        for(let i = 0; i < this.pictures.length; ++i) {
            subject.concat(Observable.fromPromise(this.resizeImage(this.pictures[i].imgElement, this.pictures[i].file)));
        }
        return subject;
    }*/

    private loadImage(img: any, file: File) {
        const id = img.src.match(/([a-zA-Z0-9-]+)$/);
        return new Promise((resolve) => {
            img.onload = () => {
                window.URL.revokeObjectURL(img.src);
                this.pictures.push({
                    id: id[0],
                    imgElement: img,
                    state: PictureState.QUEUED,
                    originalFile: file,
                    resizedFile: null
                });
                resolve();
            }
        });
    }

    private resizeImage(picture: PictureToUpload): Promise<File> {
        const img = picture.imgElement;
        const file = picture.originalFile;
        picture.state = PictureState.RESIZING;
        const resizedFile: Promise<File> = new Promise((resolve, reject) => {
            console.log('Image size: ' + img.width + "x" + img.height);
            let currentWidth = img.width;
            let currentHeight = img.height;
            let newWidth = currentWidth;
            let newHeight = currentHeight;
            if (newWidth > this.resizeWidth) {
                newWidth = this.resizeWidth
                let ratio = this.resizeWidth / currentWidth;
                newHeight = newHeight * ratio;
            }
            currentHeight = newHeight;
            if (newHeight > this.resizeHeight) {
                newHeight = this.resizeHeight;
                let ratio = this.resizeHeight / currentHeight;
                newWidth = newWidth * ratio;
            }
            if(newHeight === img.height && newWidth === img.width){
                console.log('No need to resize');
                resolve(file);
            }
            else{
                console.log('Image target size: ' + newWidth + "x" + newHeight);
                const fromCanvas: HTMLCanvasElement = document.createElement('canvas');
                const ctx = fromCanvas.getContext('2d');
                fromCanvas.width = img.width;
                fromCanvas.height = img.height;
                ctx.drawImage(img, 0, 0);

                const toCanvas: HTMLCanvasElement = document.createElement('canvas');
                toCanvas.width = newWidth;
                toCanvas.height = newHeight;

                this.ng2PicaService.resizeCanvas(fromCanvas, toCanvas, {
                    unsharpAmount: 80,
                    unsharpRadius: 0.6,
                    unsharpThreshold: 2
                }).then((resizedCanvas: HTMLCanvasElement) => {
                    resizedCanvas.toBlob((blob) => {
                        let resultFile=new Blob([blob], {type: file.type});
                        picture.state = PictureState.UPLOADING;
                        resolve(this.blobToFile(resultFile, file.name, new Date().getTime()));
                    }, file.type);
                }, (error) => reject(error));
            }
        });
        return resizedFile;
    }

    private blobToFile(blob: Blob, name:string, lastModified: number): File {
        let file: any = blob;
        file.name = name;
        file.lastModified = lastModified;
        return <File> file;
    }

    private renderPreviews() {
        if (this.boxes.length === 0) return;
        const lastBox = this.boxes[this.boxes.length -1];
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
