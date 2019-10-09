import { combineReducers } from 'redux';

import helpReducer from './help';
import searchReducer from './search';
import suggestionsReducer from './suggestions';

const rootReducer = combineReducers({
  help: helpReducer,
  search: searchReducer,
  suggestions: suggestionsReducer
});

export default rootReducer