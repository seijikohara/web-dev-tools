import Vue from "vue";
import Vuex from "vuex";

import { IAppState } from "./modules/app";

Vue.use(Vuex);

export interface State {
  app: IAppState;
}
export default new Vuex.Store<State>({});
