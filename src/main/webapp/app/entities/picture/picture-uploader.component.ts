import { Component, Input, ViewChild, ElementRef, ViewChildren, QueryList, AfterViewInit, Output, EventEmitter,
    HostListener } from '@angular/core';
import { RequestOptionsArgs, RequestOptions, Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import * as justifiedLayout from 'justified-layout';
import { Ng2PicaService } from 'ng2-pica';

enum PictureState {
    'QUEUED',
    'RESIZING',
    'UPLOADING',
    'UPLOADED',
    'ABORTED'
}

class PictureToUpload {
    id: string;
    originalHeight: number;
    originalWidth: number;
    imgElement: any;
    state: PictureState;
    originalFile: File;
    resizedFile: File;
}

class PictureUploadReport {
    processed: number;
    failed: number;
    uploaded: number;
    responses: Response[];
}

@Component({
    selector: 'jhi-image-uploader',
    templateUrl: './picture-uploader.component.html',
    styleUrls: [
        'picture.scss'
    ]
})
export class PictureUploaderComponent implements AfterViewInit {

    @Input() targetRowHeight: number;
    @Input() boxSpacing: number;
    @Input() targetRowHeightTolerance: number;
    @Input() containerPadding: number;
    @Input() maxNumRows: number;
    @Input() forceAspectRatio: boolean;
    @Input() showWidows: boolean;
    @Input() formData: any;
    @Input() fullWidthBreakoutRowCadence: boolean;
    @Input() extensions: string[];
    @Input() resizeWidth: number;
    @Input() resizeHeight: number;

    @Output() onFilesAdded: EventEmitter<number>;
    @Output() onUploaded: EventEmitter<Response>;
    @Output() onFailed: EventEmitter<void>;
    @Output() onQueueProcessed: EventEmitter<PictureUploadReport>;

    @ViewChild('fileInput') fileInput: ElementRef;
    @ViewChildren('boxes') boxesElement: QueryList<any>;

    // Inject picture state enum in template
    pictureState = PictureState;

    pictures: PictureToUpload[];
    toUpload: PictureToUpload[];
    responses: Response[];
    boxes: any[];
    galleryHeight: number;
    uploaded: number;
    failed: number;
    added: number;

    // Loading pictures progress spinner
    loadingPictures: boolean;
    loadingPicturesCurrentSize: number;
    loadingPicturesTotalSize: number;
    loadingPicturesProgress: number;

    constructor(
        private ng2PicaService: Ng2PicaService,
        private http: Http,
    ) {
        this.extensions = ['jpg', 'jpeg', 'png'];
        this.pictures = [];
        this.formData = {};
        this.boxes = [];
        this.targetRowHeight = 125;
        this.targetRowHeightTolerance = 0.25;
        this.boxSpacing = 3;
        this.maxNumRows = Number.POSITIVE_INFINITY;
        this.forceAspectRatio = false;
        this.showWidows = true;
        this.fullWidthBreakoutRowCadence = false;
        this.containerPadding = 0;
        this.loadingPicturesProgress = 0;
        this.resizeWidth = 1920;
        this.resizeHeight = 1080;
        this.onUploaded = new EventEmitter<Response>();
        this.onFilesAdded = new EventEmitter<number>();
        this.onFailed = new EventEmitter<void>();
        this.onQueueProcessed = new EventEmitter<PictureUploadReport>();
        this.responses = [];
        this.uploaded = 0;
        this.failed = 0;
        this.added = 0;
    }

    ngAfterViewInit() {
        this.galleryHeight = 0;
        setTimeout(() => {
            this.loadingPictures = false;
        }, 0);
        this.boxesElement.changes.subscribe(() => {
            console.log('Box rendered, displaying images');
            this.renderPreviews();
        })
    }

    selectFiles($event) {
        console.log($event);
        $event.stopPropagation();
        $event.preventDefault();
        const event = new MouseEvent('click', {bubbles: true});
        this.fileInput.nativeElement.dispatchEvent(event);
    }

    handleFiles($event) {
        const files = $event.srcElement.files;
        const imageLoaded = [];
        this.loadingPictures = true;
        this.toUpload = [];
        console.log(files);
        this.loadingPicturesTotalSize = 0;
        for (let i = 0; i < files.length; ++i) {
            this.loadingPicturesTotalSize += files[i].size;
        }
        this.loadingPicturesCurrentSize = 0;
        this.loadingPicturesProgress = 0;
        const toLoad = Observable.of(...files);
        const loadProcess = toLoad.mergeMap((file) => this.loadImage(file), null, 2);
        this.added += files.length;
        this.onFilesAdded.emit(files.length);
        loadProcess.subscribe(
            (toUpload: PictureToUpload) => {
                this.loadingPicturesCurrentSize += toUpload.originalFile.size;
                const progress = 100 * this.loadingPicturesCurrentSize / this.loadingPicturesTotalSize;
                if (!isNaN(progress)) {
                    this.loadingPicturesProgress = progress;
                }
            },
            (toUpload: PictureToUpload) => {
                toUpload.state = PictureState.ABORTED;
                this.failed++;
                this.onFailed.emit();
                console.log('Something wrong happened when loading picture');
            },
            () => {
                this.loadingPictures = false;
                console.log('All images loaded !');
                this.calculateImagesDimension();
                console.log('Box dimensions updated !');
                this.resizeImages();
            }
        );
    }

    retry(picture: PictureToUpload): void {
        this.added++;
        if (picture.resizedFile != null) {
            this.uploadPicture(picture);
        } else {
            this.resizeImage(picture).then(() => {
                this.uploadPicture(picture);
            }, () => {
                picture.state = PictureState.ABORTED;
                this.failed++;
                this.onFailed.emit();
            });
        }
    }

    private resizeImages() {
        const toResize = Observable.of(...this.toUpload);
        const resizeSequence = toResize.mergeMap((picture) => this.resizeImage(picture), null, 2);
        resizeSequence.subscribe(
            (picture: PictureToUpload) => {
                console.log('File resized', picture.resizedFile.size);
                this.uploadPicture(picture);
            },
            (toUpload: PictureToUpload) => {
                toUpload.state = PictureState.ABORTED;
                this.failed++;
                this.onFailed.emit();
                console.log('Something wrong happened when resizing picture');

            },
            () => {
                this.toUpload = [];
                console.log('ALL DONE');
            }
        );
    }

    private uploadPicture(picture: PictureToUpload) {
        picture.state = PictureState.UPLOADING;
        this.postImage('api/pictures/upload', picture.resizedFile, this.formData).subscribe(
            (res: Response) => {
                picture.state = PictureState.UPLOADED;
                console.log('Image uploaded !');
                this.uploaded++;
                this.responses.push(res);
                this.onUploaded.emit(res);
                if (this.added === this.uploaded + this.failed) {
                    this.onQueueProcessed.emit({
                        processed: this.added,
                        failed: this.failed,
                        uploaded: this.uploaded,
                        responses: this.responses,
                    });
                }
            },
            () => {
                picture.state = PictureState.ABORTED;
                this.failed++;
                this.onFailed.emit();
                console.log('Something wrong happened when uploading picture');
            }
        );
    }

    private loadImage(file: File): Promise<PictureToUpload> {
        const img = document.createElement('img');
        img.src = window.URL.createObjectURL(file);
        const id = img.src.match(/([a-zA-Z0-9-]+)$/);
        return new Promise((resolve) => {
            img.onload = () => {
                window.URL.revokeObjectURL(img.src);
                const toUpload: PictureToUpload = {
                    id: id[0],
                    imgElement: img,
                    originalHeight: img.height,
                    originalWidth: img.width,
                    state: PictureState.QUEUED,
                    originalFile: file,
                    resizedFile: null
                }
                this.pictures.push(toUpload);
                this.toUpload.push(toUpload);
                resolve(toUpload);
            }
        });
    }

    private resizeImage(picture: PictureToUpload): Promise<PictureToUpload> {
        picture.state = PictureState.RESIZING;
        const resizedFile: Promise<PictureToUpload> = new Promise((resolve, reject) => {
            console.log('Image size: ' + picture.originalWidth + 'x' + picture.originalWidth);
            console.log('File size:' + picture.originalFile.size);
            const currentWidth = picture.originalWidth;
            let currentHeight = picture.originalHeight;
            let newWidth = currentWidth;
            let newHeight = currentHeight;
            if (newWidth > this.resizeWidth) {
                newWidth = this.resizeWidth
                const ratio = this.resizeWidth / currentWidth;
                newHeight = newHeight * ratio;
            }
            currentHeight = newHeight;
            if (newHeight > this.resizeHeight) {
                newHeight = this.resizeHeight;
                const ratio = this.resizeHeight / currentHeight;
                newWidth = newWidth * ratio;
            }
            if (newHeight === picture.originalHeight && newWidth === picture.originalWidth) {
                console.log('No need to resize');
                picture.resizedFile = picture.originalFile;
                resolve(picture);
            } else {
                console.log('Image target size: ' + newWidth + 'x' + newHeight);
                const fromCanvas: HTMLCanvasElement = document.createElement('canvas');
                const ctx = fromCanvas.getContext('2d');
                fromCanvas.width = picture.originalWidth;
                fromCanvas.height = picture.originalHeight;
                ctx.drawImage(picture.imgElement, 0, 0);

                const toCanvas: HTMLCanvasElement = document.createElement('canvas');
                toCanvas.width = newWidth;
                toCanvas.height = newHeight;

                this.ng2PicaService.resizeCanvas(fromCanvas, toCanvas, {
                    unsharpAmount: 80,
                    unsharpRadius: 0.6,
                    unsharpThreshold: 2
                }).then((resizedCanvas: HTMLCanvasElement) => {
                    resizedCanvas.toBlob((blob) => {
                        const resultFile = new Blob([blob], {type: picture.originalFile.type});
                        const resizedFile = this.blobToFile(resultFile, picture.originalFile.name, new Date().getTime());
                        console.log('Resized File size:' + resizedFile.size);
                        picture.resizedFile = resizedFile;
                        resolve(picture);
                    }, picture.originalFile.type);
                }, (error) => reject(error));
            }
        });
        return resizedFile;
    }

    private blobToFile(blob: Blob, name: string, lastModified: number): File {
        const file: any = blob;
        file.name = name;
        file.lastModified = lastModified;
        return <File> file;
    }

    private renderPreviews() {
        if (this.boxes.length === 0) {
            return;
        }
        const lastBox = this.boxes[this.boxes.length - 1];
        setTimeout(() => {
            this.galleryHeight = lastBox.top + lastBox.height;
        }, 0)
        for (let i = 0; i < this.pictures.length; ++i) {
            const picture = this.pictures[i];
            const box = document.getElementById(picture.id);
            if (box) {
                picture.imgElement.width = box.clientWidth;
                picture.imgElement.height = box.clientHeight;
                box.appendChild(picture.imgElement);
            }
        }
    }

    private calculateImagesDimension(): any {
        const aspectRatios = this.pictures.map((picture) => picture.imgElement.width / picture.imgElement.height);
        const containerWidth = document.getElementById('justified-gallery-container').clientWidth;
        const geometry = justifiedLayout(aspectRatios, {
            containerWidth,
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

    @HostListener('window:resize', ['$event'])
    onResize(event) {
        if(this.pictures.length > 0) {
            this.calculateImagesDimension();
        }
    }

    public postImage(
        url: string,
        image: File,
        customFormData?: { [name: string]: any },
        headers?: Headers | { [name: string]: any },
        partName = 'image',
        withCredentials?: boolean
    ): Observable<Response> {

        if (!url || url === '') {
            throw new Error('Url is not set! Please set it before doing queries');
        }
        const options: RequestOptionsArgs = new RequestOptions();
        if (withCredentials) {
            options.withCredentials = withCredentials;
        }
        if (headers) {
            options.headers = new Headers(headers);
        }
        // add custom form data
        const formData = new FormData();
        for (const key in customFormData) {
            formData.append(key, customFormData[key]);
        }
        formData.append(partName, image);
        return this.http.post(url, formData, options);
    }
}
