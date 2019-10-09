import React from 'react';

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Container, Menu } from 'semantic-ui-react';

import Errors from './containers/Errors/index';
import Facets from './containers/Facets/index';
import Filters from './containers/Filters/index';
import Help from './containers/Help/index';
import Pagination from './containers/Pagination/index';
import QuerySuggestions from './containers/QuerySuggestions/index'
import ResultLayout from './containers/ResultLayout/index';
import Results from './containers/Results/index';
import SearchField from './containers/SearchField/index';
import Sort from './containers/Sort/index';
import Stats from './containers/Stats/index'

import GSearchCommonUtil from './utils/GSearchCommonUtil';
import GSearchURLUtil from './utils/GSearchURLUtil';
import { search } from "./store/actions/search";

/**
 * Redux mapping.
 *
 * @param {Object} dispatch 
 */
function mapDispatchToProps(dispatch) {
  const getSearchResults = search.request;
  return bindActionCreators({ getSearchResults }, dispatch);
}

/**
 * Liferay GSearch App component.
 */
class App extends React.Component {

  constructor(props) {

    super(props);

    // Do initial query.

    this.doInitialQuery();

    // Bind functions to this this instance.

    this.doInitialQuery = this.doInitialQuery.bind(this);
    this.scrollToTop = this.scrollToTop.bind(this);
  }

  /**
   * Does an initial query when request parameters found in the calling URL.
   * i.e. this page was probably called through a search bookmark.
   */
  doInitialQuery() {

    let initialParams = GSearchURLUtil.parseURLParameters();
    if (!GSearchCommonUtil.isEmptyObject(initialParams)) {
      this.props.getSearchResults(initialParams)
    }
  }

  /**
   * Scrolls page to results top.
   */
  scrollToTop() {
    GSearchCommonUtil.scrollPageTo('.gsearch-results-wrapper', 600);
  }

  /**
   * Render
   */
  render() {

    return (

      <Container fluid className="gsearch-container">

        <link rel="stylesheet" type="text/css" href="//cdn.jsdelivr.net/npm/semantic-ui@2.4.2/dist/semantic.min.css" />

        <Errors />

        <div className="gsearch-centered-wrapper searchfield">
          <div className="inner-wrapper">
            <Help />
            <SearchField />
          </div>
        </div>

        <Filters />
        <Facets />
        <Stats />
        <QuerySuggestions />

        <div className="gsearch-results-menu-wrapper">
          <Menu>
            <Sort />
            <Menu.Menu position='right'>
              <ResultLayout />
            </Menu.Menu>
          </Menu>
        </div>

        <div className="gsearch-results-wrapper">
          <Results />
        </div>

        <Pagination scrollToTop={this.scrollToTop} />

      </Container >
    )
  }
}

export default connect(null, mapDispatchToProps)(App);
