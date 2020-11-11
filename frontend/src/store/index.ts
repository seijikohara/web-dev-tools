import {
  createStore,
  MutationTree,
  ActionContext,
  ActionTree,
  GetterTree,
  Store as VuexStore,
  CommitOptions,
  DispatchOptions,
  createLogger,
} from "vuex";

/**
 * State
 */
export type State = {
  drawer: boolean;
};

const state: State = {
  drawer: false,
};

/**
 * Mutation
 */
export enum MutationTypes {
  SET_DRAWER = "SET_DRAWER",
}

export type Mutations<S = State> = {
  [MutationTypes.SET_DRAWER](state: S, payload: boolean): void;
};

const mutations: MutationTree<State> & Mutations = {
  [MutationTypes.SET_DRAWER](state: State, payload: boolean) {
    state.drawer = payload;
  },
};

/**
 * Action
 */
export enum ActionTypes {
  SET_DRAWER = "SET_DRAWER",
}

type AugmentedActionContext = {
  commit<K extends keyof Mutations>(
    key: K,
    payload: Parameters<Mutations[K]>[1]
  ): ReturnType<Mutations[K]>;
} & Omit<ActionContext<State, State>, "commit">;

export interface Actions {
  [ActionTypes.SET_DRAWER](
    { commit }: AugmentedActionContext,
    payload: boolean
  ): void;
}

export const actions: ActionTree<State, State> & Actions = {
  [ActionTypes.SET_DRAWER]({ commit }, payload: boolean) {
    commit(MutationTypes.SET_DRAWER, payload);
  },
};

/**
 * Getter
 */
export type Getters = {
  getDrawer(state: State): boolean;
};

export const getters: GetterTree<State, State> & Getters = {
  getDrawer: (state) => {
    return state.drawer;
  },
};

/**
 * Setup store
 */
export type Store = Omit<
  VuexStore<State>,
  "commit" | "getters" | "dispatch"
> & {
  commit<K extends keyof Mutations, P extends Parameters<Mutations[K]>[1]>(
    key: K,
    payload: P,
    options?: CommitOptions
  ): ReturnType<Mutations[K]>;
} & {
  getters: {
    [K in keyof Getters]: ReturnType<Getters[K]>;
  };
} & {
  dispatch<K extends keyof Actions>(
    key: K,
    payload: Parameters<Actions[K]>[1],
    options?: DispatchOptions
  ): ReturnType<Actions[K]>;
};

export const store = createStore({
  state,
  mutations,
  actions,
  getters,
  plugins: [createLogger()],
});

export function useStore(): Store {
  return store as Store;
}
