import React from 'react';
import { connect } from 'react-redux';

import Basic from './Layouts/Basic';
import Cards from './Layouts/Cards';
import Explain from './Layouts/Explain';
import Maps from './Layouts/Maps';
import RawDocument from './Layouts/RawDocument';
import Thumbnails from './Layouts/Thumbnails';
import UserImages from './Layouts/UserImages';

import { getItems, getResultLayout } from '../../store/reducers/search';

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
	return {
		items: getItems(state),
		resultLayout: getResultLayout(state)
	};
}

/**
 * Results view dispatcher component.
 */
class Results extends React.Component {

	render() {

		const { items, resultLayout } = this.props

		if (!items) {
			return null;
		}

		if (items.length === 0) {
			return (
				<div className="gsearch-no-results">{Liferay.Language.get('no-results')}</div>
			)
		}

		if (resultLayout === 'card') {
			return (
				<Cards items={items} />
			)
		} else if (resultLayout === 'document') {
			return (
				<RawDocument items={items} />
			)
		} else if (resultLayout === 'explain') {
			return (
				<Explain items={items} />
			)
		} else if (resultLayout === 'maps') {
			return (
				<Maps items={items} />
			)
		} else if (resultLayout === 'thumbnail') {
			return (
				<Thumbnails items={items} />
			)
		} else if (resultLayout === 'userImage') {
			return (
				<UserImages items={items} />
			)
		} else {
			return (
				<Basic items={items} />
			)
		}
	}
}

export default connect(mapStateToProps, null)(Results);
