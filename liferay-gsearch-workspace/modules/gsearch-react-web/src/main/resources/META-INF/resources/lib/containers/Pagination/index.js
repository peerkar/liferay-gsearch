import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Pagination as SUIPagination } from 'semantic-ui-react';

import { RequestParameterNames } from '../../constants/requestparameters';
import { search } from '../../store/actions/search';
import { getPagination } from '../../store/reducers/search';

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
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
	return {
		pagination: getPagination(state)
	};
}

/**
 * Pagination component.
 */
class Pagination extends React.Component {

  constructor(props) {

    super(props);
    
    // Bind functions to this instance.

    this.onPageChange = this.onPageChange.bind(this);
  }

  /**
   * Handle page change event.
   * 
   * @param {Object} event 
   * @param {String} value 
   */
  onPageChange(event, { activePage }) {

    this.props.getSearchResults({ [RequestParameterNames.PAGE]: activePage })
    this.props.scrollToTop();
  }

  /**
   * Render
   */ 
  render() {

    if (!this.props.pagination || !this.props.pagination.totalPages 
    		|| this.props.pagination.totalPages < 2) {
      return null;
    }

    const { defaultActivePage, totalPages } = this.props.pagination;

    return (
      <div className="gsearch-centered-wrapper gsearch-pagination">

        <SUIPagination
          defaultActivePage={defaultActivePage}
          ellipsisItem={null}
          onPageChange={this.onPageChange}
          totalPages={totalPages}
        />
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Pagination);
