import React from 'react';
import { Item } from 'semantic-ui-react';

/**
 * Explain view.
 * 
 * @param {*} param0 
 */
const Explain = ({ items }) => {

	return (

		<Item.Group className="explain">

			{items.map(function (item, i) {

				let key = 'ei_' + item.link;

				return (
					<Item className="gsearch-explain-item" key={key}>
						<Item.Content>
							<pre>{item.explain}</pre>
						</Item.Content>
					</Item>
				)
			})}
		</Item.Group>
	)
}

export default Explain;
