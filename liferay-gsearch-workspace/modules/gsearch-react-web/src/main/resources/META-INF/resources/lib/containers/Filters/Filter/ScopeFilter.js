import React from 'react'
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Dropdown } from 'semantic-ui-react'

import { RequestParameterNames } from '../../../constants/requestparameters';
import { scopeOptions, defaultScope } from '../../../constants/scope';
import { search } from "../../../store/actions/search";
import { getScope } from '../../../store/reducers/search';

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
		scope: getScope(state)
	};
}

/**
 * Scope filter component.
 */
class ScopeFilter extends React.Component {


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
        this.props.getSearchResults({ [RequestParameterNames.SCOPE]: value })
    }

    /**
     * Render
     */
    render() {

        const { scope } = this.props;

        return (
            <Dropdown
                compact
                defaultValue={defaultScope}
                className='gsearch-filter'
                onChange={this.handleItemSelect}
                options={scopeOptions}
                value={scope}
            />
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ScopeFilter);
