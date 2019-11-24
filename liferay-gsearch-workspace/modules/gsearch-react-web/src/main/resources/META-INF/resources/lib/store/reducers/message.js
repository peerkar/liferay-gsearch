import { setMessage, SET_MESSAGE } from '../actions/message';

const initialState = {
    text: null
}

export default function (state = initialState, action) {

    switch (action.type) {
    
        case SET_MESSAGE:

        	return Object.assign({}, state, {
            	text: action.text
            });
        default:
            return state;
    }
}

// Message selectors.

export const getMessage = (state) => state.message.text;
