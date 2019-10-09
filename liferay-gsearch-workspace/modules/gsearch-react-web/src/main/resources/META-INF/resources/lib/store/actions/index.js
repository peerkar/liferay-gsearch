export const REQUEST = 'REQUEST';
export const SUCCESS = 'SUCCESS';
export const FAILURE = 'FAILURE';

export function createRequestTypes(base) {
  return [REQUEST, SUCCESS, FAILURE].reduce(function(acc, type) {
    acc[type] = `${base}_${type}`;
    return acc;
  }, {});
}

export function createAction(type, payload = {}) {
  return Object.assign({}, payload, { type });
}