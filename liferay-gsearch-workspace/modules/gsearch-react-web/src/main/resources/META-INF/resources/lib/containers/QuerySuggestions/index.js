import React from 'react'
import { connect } from 'react-redux';
import { getMeta, getQuerySuggestions } from '../../store/reducers/search';

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
    return {
        meta: getMeta(state),
        querySuggestions: getQuerySuggestions(state)
    };
}
/**
 * Query Suggestions component
 */
class QuerySuggestions extends React.Component {


    render() {

    	if (!this.props.meta) {
    		return null;
    	}
    	
        const { originalQueryTerms, queryTerms } = this.props.meta;

        if (!originalQueryTerms) {
            return null;
        }

        const { querySuggestions } = this.props;

        return (

            <div className="gsearch-query-suggestions">

                <div className="no-results-message">{Liferay.Language.get('search-for')} <strong>{originalQueryTerms}</strong> {Liferay.Language.get('gave-no-results')}</div>

                <div className="alternate-search">{Liferay.Language.get('showing-results-for')} <strong><i>{queryTerms}</i></strong></div>

                {querySuggestions ?
                	<div>
	                    <div className="other-suggestions">{Liferay.Language.get('other-suggestions')}</div>
	                    <ul>
	                        {querySuggestions.map(function (item, i) {
	                            const key = 'qs-' + i + item
	                            return <li key={key}>{item}</li>
	                        })}
	                    </ul>
	                </div>
                : null }
              </div>
        )
    }
}

export default connect(mapStateToProps, null)(QuerySuggestions);
