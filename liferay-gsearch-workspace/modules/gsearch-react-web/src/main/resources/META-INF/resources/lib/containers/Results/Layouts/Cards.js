import React from 'react';
import { Card } from 'semantic-ui-react';

import Thumbnail from '../../../components/Image/Thumbnail';
import ResultItemUtil from '../../../utils/ResultItemUtil';

/**
 * Cards view.
 * 
 * @param {*} param0 
 */
const Cards = ({ items }) => {

    return (

        <Card.Group>

            {items.map(function (item, i) {

                let description = ResultItemUtil.getCardItemDescription(item);
                let header = ResultItemUtil.getCardHeader(item);
                let key = 'ci_' + item.link;
                let typeLabel = ResultItemUtil.getCardItemTypeLabel(item);

                return (

                    <Card className="gsearch-card" key={key} raised>
                        <Thumbnail alt={item.title} src={item.imageSrc} />
                        <Card.Content>
                            {typeLabel}
                            {header}
                            <Card.Meta className="username">{item.userName}, {item.date}</Card.Meta>
                            {description}
                        </Card.Content>
                    </Card>
                )
            })}
        </Card.Group>
    )
}

export default Cards;
