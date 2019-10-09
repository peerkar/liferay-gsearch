import React from 'react'
import config from 'react-global-configuration';
import { connect } from 'react-redux';

import ScopeFilter from './Filter/ScopeFilter';
import TimeFilter from './Filter/TimeFilter';
import { ConfigKeys } from '../../constants/configkeys';
import { getItems } from '../../store/reducers/search';

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
	return {
        shouldRender: getItems(state)
	};
}

/**
 * Filters component.
 */
class Filters extends React.Component {

    constructor(props) {

        super(props);

        // Component configuration.

        this.filterConfig = config.get(ConfigKeys.FILTER);
    }

    /**
     * Render.
     */
    render() {

        if (!this.props.shouldRender) {
            return null;
        }

        let scopeFilter = '';

        if (this.filterConfig.showScopeFilter) {
            scopeFilter = <ScopeFilter />;
        }

        let timeFilter = '';

        if (this.filterConfig.showTimeFilter) {
            timeFilter = <TimeFilter />;
        }

        return (
            <div className="gsearch-centered-wrapper gsearch-filters">
                {scopeFilter}
                {timeFilter}
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(Filters);
