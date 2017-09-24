import { PictureState } from './picture-state.enum';

export class PictureToUpload {
    id: string;
    originalHeight: number;
    originalWidth: number;
    imgElement: any;
    state: PictureState;
    originalFile: File;
    resizedFile: File;
}
