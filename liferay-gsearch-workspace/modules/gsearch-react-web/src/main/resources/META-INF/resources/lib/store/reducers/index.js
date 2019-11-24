import { combineReducers } from 'redux';

import helpReducer from './help';
import messageReducer from './message';
import searchReducer from './search';
import suggestionsReducer from './suggestions';

const rootReducer = combineReducers({
  help: helpReducer,
  message: messageReducer,
  search: searchReducer,
  suggestions: suggestionsReducer
});

export default rootReducer