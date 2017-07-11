import {ToastOptions} from 'ng2-toastr';

export class CustomToasts extends ToastOptions {
    enableHTML= true;
    toastLife = 2000;
    positionClass = 'toast-top-center';
}
