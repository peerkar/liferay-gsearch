import React from 'react'
import config from 'react-global-configuration';

import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Dropdown } from "semantic-ui-react";

import { ConfigKeys } from '../../constants/configkeys';
import { RequestParameterNames } from '../../constants/requestparameters';
import { search } from '../../store/actions/search';
import { getItems, getSortField } from '../../store/reducers/search';

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
function mapStateToProps(state, ownProps) {
	return {
		shouldRender: (getItems(state) &&  getItems(state).length),
		sortField: getSortField(state)
	};
}

/**
 * Sort field component.
 */
class SortField extends React.Component {

	constructor(props) {

		super(props);

		// Component configuration.

		this.sortFieldConfig = config.get(ConfigKeys.SORT);

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
		this.props.getSearchResults({ [RequestParameterNames.SORT_FIELD]: value })
	}

	render() {

		const { shouldRender, sortField } = this.props;

		if (!shouldRender) {
			return null;
		}

		return (
			<div className="gsearch-sort-field-menu">
				<span className="gsearch-label">Sort by: {' '}</span>
				<Dropdown
					button
					defaultValue={this.sortFieldConfig.defaultValue}
					floating
					labeled
					onChange={this.handleItemSelect}
					options={this.sortFieldConfig.options}
					value={sortField}
				/>
			</div>
		)
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SortField);
