import React from 'react';
import config from 'react-global-configuration';

import { Card, Item, Label } from "semantic-ui-react";

import GSearchCommonUtil from './GSearchCommonUtil';
import Thumbnail from '../components/Image/Thumbnail';
import { ConfigKeys } from '../constants/configkeys';

/**
 * GSearch result item utility class.
 */
class ResultItemUtil {

	static formatAssetCategories(values) {
		return values.map((value, j) => {
			let key = Date.now().toString() + "_asc_" + j;
			return <span className="label meta-item category" key={key}>{value}</span>
		})
	}

	static formatAssetTags(values) {
		return values.map((value, j) => {
			let key = Date.now().toString() + "ast" + j;
			return <span className="label meta-item tag" key={key}>{value}</span>
		})
	}

	static getAssetCategoriesMeta(item) {

		let value = '';

		if (config.get(ConfigKeys.RESULT_ITEM).showAssetCategories && item.assetCategoryTitles) {

			let categoryItems = ResultItemUtil.formatAssetCategories(item.assetCategoryTitles);

			value = <Item.Meta className="categories">
				<span className="meta-key">Categories:</span> {categoryItems}
			</Item.Meta>;
		}

		return value;
	}

	static getAssetTagsMeta(item) {

		let value = '';

		if (config.get(ConfigKeys.RESULT_ITEM).showAssetTags && item.assetTagNames) {

			let tagItems = ResultItemUtil.formatAssetTags(item.assetTagNames);

			value = <Item.Meta className="tags">
				<span className="meta-key">Tags:</span> {tagItems}
			</Item.Meta>;
		}

		return value;
	}

	static getAuthorMeta(item) {

		let value = '';

		if (config.get(ConfigKeys.RESULT_ITEM).showAuthor) {

			value = <Item.Meta className="author">
				<span className="meta-key">Author:</span> {item.userName}
			</Item.Meta>;
		}

		return value;
	}

	static getCardItemDescription(item) {

		let descriptionTag = '';
		
		let description = item.content_highlight ? item.content_highlight : item.description;

		if (item.type.toUpperCase() === 'FILE') {

			let dimensions = '';
			if (item.metadata.dimensions) {
				dimensions += `${item.metadata.dimensions}, `;
			}
			descriptionTag = <div className="file-metadata">{dimensions} {item.metadata.size}</div>

		} else {
			descriptionTag = <span dangerouslySetInnerHTML={{ __html: description }}></span>
		}
		
		return <Card.Description>{descriptionTag}</Card.Description>
	}

	static getCardItemTypeLabel(item) {

		let type = '';

		if (item.type.toUpperCase() === 'FILE') {
			type = item.metadata.format;
		} else {
			type = item.type;
		}

		return <Label className="type-label" color='orange' attached='top left'>{type}</Label>;
	}

	static getCardHeader(item) {

		let value = '';

		let href = item.link
		if (config.get(ConfigKeys.RESULT_ITEM).appendRedirect) {
			href += item.redirect;
		}

		if (config.get(ConfigKeys.RESULT_ITEM).showLink) {
			if (config.get(ConfigKeys.CLICK_TRACKING).enabled) {
				
				value = <Card.Header as='a' className="link" href={href} onClick={GSearchCommonUtil.trackClick} data-trackid={item.entryClassPK}>
							<span dangerouslySetInnerHTML={{ __html: item.title }}></span>
						</Card.Header>

			} else {
				value = <Card.Header as='a' className="link" href={href}>
							<span dangerouslySetInnerHTML={{ __html: item.title }}></span>
						</Card.Header>
			}
		}
		return value;
	}	
	
	static getDescription(item) {
		
		let description = item.content_highlight ? item.content_highlight : item.description;

		return <Item.Description><span className='date'>{item.date} -</span>
			<span dangerouslySetInnerHTML={{ __html: description }}></span>
		</Item.Description>;
	}

	static getItemImage(item, size) {

		let value = '';

		if (item.imageSrc) {
			value = <Item.Image size={size} src={item.imageSrc} />;
		}

		return value;
	}

	static getLink(item) {

		let value = '';

		let href = item.link
		if (config.get(ConfigKeys.RESULT_ITEM).appendRedirect) {
			href += item.redirect;
		}

		if (config.get(ConfigKeys.RESULT_ITEM).showLink) {
			if (config.get(ConfigKeys.CLICK_TRACKING).enabled) {
				value = <Item.Meta as='a' className="link" href={href} onClick={GSearchCommonUtil.trackClick} data-trackid={item.entryClassPK}>
					{item.link}
				</Item.Meta>;
			} else {
				value = <Item.Meta as='a' className="link" href={href}>
					{item.link}
				</Item.Meta>;
			}
		}
		return value;
	}

	static getThumbnail(item) {

		let value = '';

		if (item.imageSrc) {
			value = <Thumbnail alt={item.title} src={item.imageSrc} />;
		}

		return value;
	}

	static getTitle(item) {

		let typeLabel = ResultItemUtil.getTypeLabel(item);
		
		let title = item.title_highlight ? item.title_highlight : item.title;

		// Result item tagger example.
		
		let titleTag = '';
		if (item.official_content) {
            titleTag = <span title={Liferay.Language.get('official-article')} className="glyphicon glyphicon-check official-content" />
		}		
		
		let href = item.link
		if (config.get(ConfigKeys.RESULT_ITEM).appendRedirect) {
			href += item.redirect;
		}

		if (config.get(ConfigKeys.CLICK_TRACKING).enabled) {
			return <Item.Header as='a' href={href} onClick={GSearchCommonUtil.trackClick} data-trackid={item.entryClassPK}>
				{typeLabel}
				<span dangerouslySetInnerHTML={{ __html: title }}></span>
				{titleTag}
			</Item.Header>;
		} else {
			return <Item.Header as='a' href={href}>
				{typeLabel}
				<span dangerouslySetInnerHTML={{ __html: title }}></span>
			</Item.Header>;
		}
	}

	static getTypeLabel(item) {

		let value = '';

		if (config.get(ConfigKeys.RESULT_ITEM).showType) {
			value = <span className="type">[ {item.type} ]</span>;
		}

		return value;
	}

	static getUserImage(item, size) {

		let value = '';

		if (item.userPortraitUrl) {
			value = <Item.Image circular size={size} src={item.userPortraitUrl} />;
		} else if (item.userInitials) {
			value = <span className="initials">{item.userInitials}</span>;
		}

		return value;
	}
}

export default ResultItemUtil;