import 'regenerator-runtime/runtime';
import { applyMiddleware, compose, createStore } from 'redux';
import createSagaMiddleware from 'redux-saga';
import rootReducer from './reducers/index';
import rootSaga from './sagas/index';

export function configureStore() {

  const sagaMiddleware = createSagaMiddleware();

  // Todo: disable for production

  const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

  const store = createStore(rootReducer, composeEnhancers(
    applyMiddleware(sagaMiddleware)
  ));

  sagaMiddleware.run(rootSaga);

  return store;
}