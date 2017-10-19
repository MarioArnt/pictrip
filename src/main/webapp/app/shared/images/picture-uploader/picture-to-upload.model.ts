import { PictureState } from './picture-state.enum';

export class PictureToUpload {
    id: string;
    dbid: number;
    originalHeight: number;
    originalWidth: number;
    imgElement: any;
    state: PictureState;
    originalFile: File;
    resizedFile: File;
}
