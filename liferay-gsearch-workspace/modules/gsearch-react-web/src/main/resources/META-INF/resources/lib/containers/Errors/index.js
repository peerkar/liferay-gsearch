import React from 'react'
import { connect } from 'react-redux';

import Error from './Error';

import { getHelpError } from '../../store/reducers/help';
import { getSearchError } from '../../store/reducers/search';
import { getSuggestionsError } from '../../store/reducers/suggestions';

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
  return {
    helpError: getHelpError(state),
    searchError: getSearchError(state),
    suggestionsError: getSuggestionsError(state)
  };
}

/**
 * Error  component.
 */
class Errors extends React.Component {

  constructor(props) {
    super(props);
  }

  /**
   * Get all errors in an array.
   */
  getErrors() {
    return [
      this.props.helpError,
      this.props.searchError,
      this.props.suggestionsError
    ];
  }

  /**
   * Render.
   */
  render() {

    const errors = this.getErrors();

    return (
      <div className="gsearch-errors">
        {errors.map(function (error, i) {

            if (error.message) {
              return <Error error={error} />;
            }
          })
        }
      </div>      
    )
  }
}
export default connect(mapStateToProps, null)(Errors);
