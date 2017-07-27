import { Injectable } from '@angular/core';
import { MdDialog, MdDialogRef } from '@angular/material';
import { JhiLoginModalComponent } from './login.component';

@Injectable()
export class LoginModalService {

    private loginModalRef: MdDialogRef<JhiLoginModalComponent>;
    constructor(
        public dialog: MdDialog
    ) {}

    open() {
        this.loginModalRef = this.dialog.open(JhiLoginModalComponent);
        return;
    }
}
