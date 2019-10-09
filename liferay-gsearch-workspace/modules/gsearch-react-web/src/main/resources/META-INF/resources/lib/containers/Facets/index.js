import React from 'react'
import config from 'react-global-configuration';

import { connect } from 'react-redux';

import Facet from './Facet';
import { ConfigKeys } from '../../constants/configkeys';
import { getFacets } from '../../store/reducers/search';


/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
	return {
		facets: getFacets(state)
	};
}

/**
 * Facets component.
 */
class Facets extends React.Component {

    constructor(props) {

        super(props);

        // Component configuration.

        this.facetConfig = config.get(ConfigKeys.FACET);
    }

    /**
     * Render
     */
    render() {

        const { facets } = this.props;

        if (!facets || facets.length == 0) {
            return null;
        }

        return (
            <div className="gsearch-centered-wrapper gsearch-facets">
                {facets.map(function (facet, i) {
                    return <Facet config={this.facetConfig[facet.param]} facet={facet} key={i} />
                }, this)}
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(Facets);
