// File: toast.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  constructor() { }

  showToast({ error, defaultMsg: defaultMessage, title = '', delay = 5000 }: {
    error?: any, defaultMsg: string, title?: string, delay?: number
  }) {
    let message = defaultMessage;
    let toastClass = 'bg-danger';

    if (!error) {
      toastClass = 'bg-success';
    }

    if (error) {
      if (error.error && error.error.message) {
        message = error.error.message;
      } else if (typeof error === 'string') {
        message = error;
      }
    }

    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
      toastContainer = document.createElement('div');
      toastContainer.id = 'toast-container';
      toastContainer.style.position = 'fixed';
      toastContainer.style.top = '20px';
      toastContainer.style.right = '20px';
      toastContainer.style.zIndex = '9999';
      document.body.appendChild(toastContainer);
    }

    const toast = document.createElement('div');
    toast.classList.add('toast', 'show', toastClass, 'text-white');
    toast.style.minWidth = '300px';
    toast.style.marginBottom = '1rem';
    toast.style.borderRadius = '4px';
    toast.style.boxShadow = '0 2px 10px #0000001A';
    toast.style.overflow = 'hidden';
    toast.style.display = 'flex';
    toast.style.alignItems = 'center';
    toast.style.padding = '8px 12px';
    toast.style.gap = '12px';

    const textWrap = document.createElement('div');
    textWrap.style.flex = '1 1 auto';
    textWrap.style.minWidth = '0';

    const titleEl = document.createElement('span');
    titleEl.style.fontWeight = '600';
    titleEl.style.marginRight = '8px';
    titleEl.textContent = title;

    const msgEl = document.createElement('span');
    msgEl.style.whiteSpace = 'nowrap';
    msgEl.style.overflow = 'hidden';
    msgEl.style.textOverflow = 'ellipsis';
    msgEl.style.display = 'inline-block';
    msgEl.style.verticalAlign = 'middle';
    msgEl.style.maxWidth = '100%';
    msgEl.textContent = message;

    textWrap.appendChild(titleEl);
    textWrap.appendChild(msgEl);

    const closeBtn = document.createElement('button');
    closeBtn.type = 'button';
    closeBtn.innerHTML = '&times;';
    closeBtn.setAttribute('aria-label', 'Đóng thông báo');
    closeBtn.style.background = 'none';
    closeBtn.style.border = 'none';
    closeBtn.style.color = 'white';
    closeBtn.style.cursor = 'pointer';
    closeBtn.style.fontSize = '18px';
    closeBtn.style.lineHeight = '1';
    closeBtn.style.padding = '4px 8px';
    closeBtn.style.flex = '0 0 auto';

    toast.appendChild(textWrap);
    toast.appendChild(closeBtn);
    toastContainer.appendChild(toast);

    const timeoutId = setTimeout(() => { toast.remove() }, delay);
    closeBtn.addEventListener('click', () => { clearTimeout(timeoutId), toast.remove() });
  }

}
