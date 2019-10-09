import { select, takeLatest } from 'redux-saga/effects';
import { REQUEST } from '../actions/index';
import * as searchActions from '../actions/search';
import * as api from '../api/index';
import { fetchEntity } from './index';

import { getSearchParams } from '../reducers/search'

/**
 * Fetches search results.
 */
export function* fetchSearchResults(payload) {

  let searchParams = yield select(getSearchParams);

  const request = api.getSearchResults;
  yield fetchEntity(request, searchActions.search, searchParams);
}

/**
 * Search watcher.
 */
export function* watchSearch() {
  yield takeLatest(searchActions.SEARCH[REQUEST], fetchSearchResults);
}
