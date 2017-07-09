import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class LoginModalService {

    // Observable string sources
    private showLoginModal = new Subject<void>();
    private closeLoginModal = new Subject<void>();

    // Observable string streams
    showLoginModal$ = this.showLoginModal.asObservable();
    closeLoginModal$ = this.closeLoginModal.asObservable();
    constructor(
    ) {
    }

    open() {
        console.log('Call service to open modal');
        this.showLoginModal.next();
        return;
    }

    close() {
        console.log('Call service to close modal');
        this.closeLoginModal.next();
    }
}
