import { createAction, createRequestTypes, FAILURE, REQUEST, SUCCESS } from './index';

export const SEARCH = createRequestTypes('SEARCH');

export const search = {
    request: searchParams => createAction(SEARCH[REQUEST], { searchParams }),
    success: (response, searchParams ) => createAction(SEARCH[SUCCESS], {  response, searchParams  }),
    failure: error => createAction(SEARCH[FAILURE], { error })
}
