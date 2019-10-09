import { createAction, createRequestTypes, FAILURE, REQUEST, SUCCESS } from './index';

export const SUGGESTIONS = createRequestTypes('SUGGESTIONS');

export const suggestions = {
    request: keywords => createAction(SUGGESTIONS[REQUEST], { keywords }),
    success: response => createAction(SUGGESTIONS[SUCCESS], { response }),
    failure: error => createAction(SUGGESTIONS[FAILURE], { error })
}

