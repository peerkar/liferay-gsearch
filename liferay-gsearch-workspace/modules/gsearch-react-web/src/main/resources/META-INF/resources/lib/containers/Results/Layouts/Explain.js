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

                // Rerender always

                let key = 'ei_' +  Math.floor(Math.random() * 1000000);

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
