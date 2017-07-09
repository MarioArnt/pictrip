import { Component, AfterViewInit, Renderer, ElementRef, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { MaterializeAction } from 'angular2-materialize';
import { LoginService } from './login.service';
import { LoginModalService } from './login-modal.service';
import { StateStorageService } from '../auth/state-storage.service';
import { SocialService } from '../social/social.service';

@Component({
    selector: 'jhi-login-modal',
    templateUrl: './login.component.html',
    styleUrls: [
        'login.scss'
    ]
})
export class JhiLoginModalComponent implements AfterViewInit {
    authenticationError: boolean;
    password: string;
    rememberMe: boolean;
    username: string;
    credentials: any;
    modalActions: EventEmitter<string|MaterializeAction>;
    constructor(
        private eventManager: JhiEventManager,
        private loginService: LoginService,
        private stateStorageService: StateStorageService,
        private elementRef: ElementRef,
        private renderer: Renderer,
        private socialService: SocialService,
        private router: Router,
        private loginModalService: LoginModalService) {
        this.modalActions = new EventEmitter<string|MaterializeAction>();
        this.credentials = {};
        loginModalService.showLoginModal$.subscribe(() => {
            console.log('Show login modal');
            this.modalActions.emit({
                action: 'modal',
                params: ['open'],
            });
        });
        loginModalService.closeLoginModal$.subscribe(() => {
            console.log('Close login modal');
            this.modalActions.emit({
                action: 'modal',
                params: ['close'],
            });
        });
    }

    ngAfterViewInit() {
        // this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#username'), 'focus', []);
    }

    closeModal() {
        this.loginModalService.close();
    }

    cancel() {
        this.credentials = {
            username: null,
            password: null,
            rememberMe: true
        };
        this.authenticationError = false;
        // this.activeModal.dismiss('cancel');
    }

    login() {
        this.loginService.login({
            username: this.username,
            password: this.password,
            rememberMe: this.rememberMe
        }).then(() => {
            this.authenticationError = false;
            // this.activeModal.dismiss('login success');
            if (this.router.url === '/register' || (/activate/.test(this.router.url)) ||
                this.router.url === '/finishReset' || this.router.url === '/requestReset') {
                this.router.navigate(['']);
            }

            this.eventManager.broadcast({
                name: 'authenticationSuccess',
                content: 'Sending Authentication Success'
            });

            // // previousState was set in the authExpiredInterceptor before being redirected to login modal.
            // // since login is succesful, go to stored previousState and clear previousState
            const redirect = this.stateStorageService.getUrl();
            if (redirect) {
                this.router.navigate([redirect]);
            }
        }).catch(() => {
            this.authenticationError = true;
        });
    }

    register() {
        // this.activeModal.dismiss('to state register');
        this.router.navigate(['/register']);
    }

    requestResetPassword() {
        // this.activeModal.dismiss('to state requestReset');
        this.router.navigate(['/reset', 'request']);
    }
}
