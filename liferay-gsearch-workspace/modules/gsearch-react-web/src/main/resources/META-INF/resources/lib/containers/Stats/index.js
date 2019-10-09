import React from 'react'
import { connect } from 'react-redux';
import { getMeta, getPagination } from '../../store/reducers/search';

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
    return {
        meta: getMeta(state),
        pagination: getPagination(state)
    };
}

/**
 * Search statistics component.
 */
class Stats extends React.Component {

    render() {

        if (!this.props.meta || this.props.meta.totalHits === 0) {
            return null;
        }

        const { executionTime, totalHits } = this.props.meta;
        const { defaultActivePage, totalPages } = this.props.pagination;

        return (
            <div className="gsearch-centered-wrapper gsearch-stats">
                {Liferay.Language.get('page')} {defaultActivePage} / {totalPages} {Liferay.Language.get('of-hits-pre')} {totalHits} {Liferay.Language.get('of-hits-post')}. {Liferay.Language.get('search-took')} {executionTime} {Liferay.Language.get('seconds')}.
        </div>
        )
    }
}

export default connect(mapStateToProps, null)(Stats);
