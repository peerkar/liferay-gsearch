import { takeLatest } from 'redux-saga/effects';
import { REQUEST } from '../actions/index';
import * as helpActions from '../actions/help';
import * as api from '../api/index';
import { fetchEntity } from './index';

/**
 * Fetches help.
 */
export function* fetchHelp() {
    const request = api.getHelp;
    yield fetchEntity(request, helpActions.help);
}

/**
 * Help watcher.
 */
export function* watchHelp() {
    yield takeLatest(helpActions.HELP[REQUEST], fetchHelp);
}
