import React from 'react';
import { Item } from 'semantic-ui-react';

import ResultItemUtil from '../../../utils/ResultItemUtil';

/**
 * Basic list view.
 * 
 * @param {*} param0 
 */
const Basic = ({ items }) => {

	return (

		<Item.Group className="basic">

			{items.map(function (item, i) {

				let authorMeta = ResultItemUtil.getAuthorMeta(item);
				let categoriesMeta = ResultItemUtil.getAssetCategoriesMeta(item);
				let description = ResultItemUtil.getDescription(item);
				let key = 'bi_' + item.link;
				let link = ResultItemUtil.getLink(item);
				let tagsMeta = ResultItemUtil.getAssetTagsMeta(item);
				let title = ResultItemUtil.getTitle(item);

				return (
					<Item className="gsearch-basic-item" key={key}>
						<Item.Content>
							{title}
							{link}
							{description}
							{tagsMeta}
							{categoriesMeta}
							{authorMeta}
						</Item.Content>
					</Item>
				)
			})}
		</Item.Group>
	)
}

export default Basic;