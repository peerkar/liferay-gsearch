import React from 'react';
import { Item } from "semantic-ui-react";

import ResultItemUtil from '../../../utils/ResultItemUtil';

/**
 * User images view.
 * 
 * @param {*} param0 
 */
const UserImages = ({ items }) => {

	return (

		<Item.Group>

			{items.map(function (item, i) {

				let categoriesMeta = ResultItemUtil.getAssetCategoriesMeta(item);
				let description = ResultItemUtil.getDescription(item);
				let key = 'ui_' + item.link;
				let link = ResultItemUtil.getLink(item);
				let tagsMeta = ResultItemUtil.getAssetTagsMeta(item);
				let title = ResultItemUtil.getTitle(item);
				let userImage = ResultItemUtil.getUserImage(item, 'tiny');

				return (
					<Item className='gsearch-userimage-item' key={key}>
						<Item.Meta className="user-data">
							{userImage} <span className="username">{item.userName}</span>, {item.date}
						</Item.Meta>
						<Item.Content>
							{title}
							{link}
							{description}
							{tagsMeta}
							{categoriesMeta}
						</Item.Content>
					</Item>
				)
			})}
		</Item.Group>
	)
}

export default UserImages;
