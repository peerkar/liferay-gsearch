import React from 'react';
import JSONPretty from 'react-json-prettify';
import { Item } from 'semantic-ui-react';

/**
 * Raw document view.
 * 
 * @param {*} param0 
 */
const RawDocument = ({ items }) => {

	return (

		<Item.Group className="document">

			{items.map(function (item, i) {

				let key = 'di_' + item.link;

				return (
					<Item className="gsearch-document-item" key={key}>
						<Item.Content>
							<JSONPretty json={item.document} padding={4} />
						</Item.Content>
					</Item>
				)
			})}
		</Item.Group>
	)
}

export default RawDocument;
