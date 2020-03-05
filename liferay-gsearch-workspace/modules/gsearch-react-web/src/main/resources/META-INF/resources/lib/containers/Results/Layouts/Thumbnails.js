import React from 'react';
import { Item } from 'semantic-ui-react';

import ResultItemUtil from '../../../utils/ResultItemUtil';

/**
 * Thumbnails view.
 * 
 * @param {*} param0 
 */
const Thumbnails = ({ items }) => {

    return (

        <Item.Group>

            {items.map(function (item, i) {

                let authorMeta = ResultItemUtil.getAuthorMeta(item);
                let categoriesMeta = ResultItemUtil.getAssetCategoriesMeta(item);
                let description = ResultItemUtil.getDescription(item);

                // Rerender always

                let key = 'ti_' +  Math.floor(Math.random() * 1000000);
                let link = ResultItemUtil.getLink(item);
                let tagsMeta = ResultItemUtil.getAssetTagsMeta(item);
                let thumbnail = ResultItemUtil.getThumbnail(item);
                let title = ResultItemUtil.getTitle(item);

                return (
                    <Item className='gsearch-thumbnail-item' key={key}>
                        {thumbnail}
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

export default Thumbnails;
