export type ChainedWhen<T, R> = {
  on: <A>(pred: (v: T) => boolean, fn: () => A) => ChainedWhen<T, R | A>;
  otherwise: <A>(fn: () => A) => R | A;
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const match = <T, R>(val: any): ChainedWhen<T, R> => ({
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  on: <A>(pred: (v: T) => boolean, fn: () => A) => match<T, R | A>(val),
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  otherwise: <A>(fn: () => A): A | R => val
});

export const chain = <T, R>(val: T): ChainedWhen<T, R> => ({
  on: <A>(pred: (v: T) => boolean, fn: () => A) =>
    pred(val) ? match(fn()) : chain<T, A | R>(val),
  otherwise: <A>(fn: () => A) => fn()
});

export const when = <T>(val: T) => ({
  on: <A>(pred: (v: T) => boolean, fn: () => A) =>
    pred(val) ? match<T, A>(fn()) : chain<T, A>(val)
});

export const eq = <T>(val1: T) => (val2: T) => val1 === val2;
