import {
  Mutation,
  Action,
  VuexModule,
  getModule,
  Module
} from "vuex-module-decorators";
import store from "@/store";

export interface AppState {
  drawer: boolean;
}
@Module({ dynamic: true, store, name: "app", namespaced: true })
class App extends VuexModule implements AppState {
  // state
  drawer = false;

  // mutation
  @Mutation
  public SET_DRAWER(value: boolean) {
    this.drawer = value;
  }

  // action
  @Action({})
  public toggleDrawer() {
    this.SET_DRAWER(!this.drawer);
  }
  @Action({})
  public setDrawer(value: boolean) {
    this.SET_DRAWER(value);
  }

  // getter
  public get getDrawer(): boolean {
    return this.drawer;
  }
}

export const appModule = getModule(App);
