import React from 'react'

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Dropdown } from 'semantic-ui-react'

import { search } from "../../store/actions/search";
import { getFacetValue } from '../../store/reducers/search';

/**
 * Redux mapping.
 *
 * @param {Object} dispatch 
 */
function mapDispatchToProps(dispatch) {
    const getSearchResults = search.request;
    return bindActionCreators({ getSearchResults }, dispatch);
}

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state, ownProps) {
    return {
      value: getFacetValue(state, ownProps.facet.param) ? getFacetValue(state, ownProps.facet.param) : []
    };
  }

/**
 * Single facet component.
 */
class Facet extends React.Component {

    constructor(props) {

        super(props);

        this.state = {
            open: false,
        }

        // Bind functions to this instance.

        this.handleItemSelect = this.handleItemSelect.bind(this);
        this.onBlur = this.onBlur.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onFocus = this.onFocus.bind(this);
        this.renderLabel = this.renderLabel.bind(this);
    }

    /**
     * Handle item selection event.
     * 
     * @param {Object} event 
     * @param {String} value 
     */
    handleItemSelect(event, { value }) {
        this.props.getSearchResults({ [this.props.facet.param]: value })
    }

    /**
     * Handle blur event.
     * 
     * @param {Object} event 
     */
    onBlur(event) {
        this.setState({ open: false });
    }

    /**
     * Handle click event.
     * 
     * @param {Object} event 
     */
    onClick(event) {
        this.setState({ open: true });
    }

    onFocus(event, data) {
        this.setState({ focused: true });
    }

    /**
     * Render selected facet (label).
     * 
     * @param {Object} item 
     */
    renderLabel(item) {

        let color = 'grey';
        let content = item.text_ ? item.text_ : item.value;
        let icon = '';

        if (this.props.config) {
            if (this.props.config.color && this.props.config.color.length > 0) {
                color = this.props.config.color;
            }
            if (this.props.config.icon && this.props.config.icon.length > 0) {
                icon = this.props.config.icon;
            }
        }

        return ({
            color: color,
            content: content,
            icon: icon
        })
    }

    /**
     * Render
     */
    render() {

        const { open } = this.state;
        const { value } = this.props;

        return (
            <Dropdown
                className='gsearch-facet'
                clearable
                closeOnBlur
                deburr
                lazyLoad
                multiple
                onClick={this.onClick}
                onFocus={this.onFocus}
                onMouseLeave={this.onBlur}
                onBlur={this.onBlur}
                onChange={this.handleItemSelect}
                open={this.state.open}
                options={this.props.facet.values}
                renderLabel={this.renderLabel}
                search
                selection
                text={open ? Liferay.Language.get('search') : this.props.facet.anyOption}
                value={value}
            />
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Facet);