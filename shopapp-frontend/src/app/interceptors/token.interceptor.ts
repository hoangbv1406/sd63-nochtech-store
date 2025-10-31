// File: token.interceptor.ts
import { inject } from '@angular/core';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { TokenService } from '../services/token.service';

export function tokenInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) {
  const authToken = inject(TokenService).getToken();
  if (authToken?.trim()) {
    const newReq = req.clone({ headers: req.headers.set('Authorization', `Bearer ${authToken}`) });
    return next(newReq);
  }
  return next(req);
}
