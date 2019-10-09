import { createAction, createRequestTypes, FAILURE, REQUEST, SUCCESS } from './index';

export const HELP = createRequestTypes('HELP');

export const help = {
    request: () => createAction(HELP[REQUEST], {}),
    success: response => createAction(HELP[SUCCESS], { response }),
    failure: error => createAction(HELP[FAILURE], { error })
}

