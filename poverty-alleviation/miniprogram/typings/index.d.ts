/// <reference path="./app.d.ts" />

declare namespace WechatMiniprogram {
  interface PageOptions {
    data?: Record<string, any>
    onLoad?(query: Record<string, string>): void
    onShow?(): void
    onReady?(): void
    onHide?(): void
    onUnload?(): void
    [key: string]: any
  }
  function Page<T extends PageOptions>(options: T): void
  function App<T extends IAppOption>(options: T): void
  function getApp<T = IAppOption>(): T
}
