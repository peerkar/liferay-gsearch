import { FAILURE, REQUEST, SUCCESS } from '../actions/index';
import { HELP } from '../actions/help';

const initialState = {
    error: {},
    isLoading: false,
    text: ''
}

export default function (state = initialState, action) {

    switch (action.type) {

        case HELP[REQUEST]:
            return Object.assign({}, state, {
                isLoading: true
            });
        case HELP[SUCCESS]:
            return {
                error: {},
                isLoading: false,
                text: action.response
            };
        case HELP[FAILURE]:
            return Object.assign({}, state, {
                error: action.error,
                isLoading: false
            });
        default:
            return state;
    }
}

// Help selectors.

export const getHelpError = (state) => state.help.error;
export const getHelpText = (state) => state.help.text;
export const isHelpLoading = (state) => state.help.isLoading;
