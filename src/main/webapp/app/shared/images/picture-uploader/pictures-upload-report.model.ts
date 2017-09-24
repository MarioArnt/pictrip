import { Response } from '@angular/http';

export class PictureUploadReport {
    processed: number;
    failed: number;
    uploaded: number;
    responses: Response[];
}
