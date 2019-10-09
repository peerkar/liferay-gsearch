import { all, call, fork, put } from 'redux-saga/effects';

import { watchHelp } from './help';
import { watchSearch } from './search';
import { watchSuggestions } from './suggestions';

export default function* () {
  yield all([
    fork(watchHelp),
    fork(watchSearch),
    fork(watchSuggestions)
  ]);
}

/**
 * Fetch.
 *  - request: API call
 *  - entity: help | search | suggestions 
 * 
 * @param {Object} request 
 * @param {Object} entity 
 * @param  {...any} args 
 */
export function* fetchEntity(request, entity, ...args) {

  try {

    const response = yield call(request, args);
    
    yield put(entity.success(response, ...args));

  } catch (error) {

    yield put(entity.failure(error, ...args));
  }
}
