import React from 'react'

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Dropdown } from "semantic-ui-react";

import { RequestParameterNames } from '../../constants/requestparameters';
import { search } from "../../store/actions/search";
import { getItems, getResultLayout, getResultLayoutOptions } from '../../store/reducers/search';

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
		resultLayout: getResultLayout(state),
		resultLayoutOptions: getResultLayoutOptions(state),
		shouldRender: (getItems(state) &&  getItems(state).length),
	};
}

/**
 * Result layout options component.
 */
class ResultLayout extends React.Component {

  constructor(props) {

    super(props);

    // Bind functions to this instance.

    this.handleItemSelect = this.handleItemSelect.bind(this);
  }

  /**
   * Handle item selection event.
   * 
   * @param {Object} event 
   * @param {String} value 
   */
  handleItemSelect(event, { value }) {
    this.props.getSearchResults({ [RequestParameterNames.RESULT_LAYOUT]: value })
  }

  /**
   * Render.
   */
  render() {

    const { resultLayout, resultLayoutOptions, shouldRender  } = this.props;

    if (!shouldRender) {
      return null;
    }

    return (

      <Dropdown
        basic
        button
        className='gsearch-result-layout-menu icon'
        header={Liferay.Language.get('result-layout')}
        icon='unhide icon-eye'
        floating
        onChange={this.handleItemSelect}
        options={resultLayoutOptions}
        value={resultLayout}
      />
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ResultLayout);
