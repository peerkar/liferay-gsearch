import React from 'react'

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Dropdown } from 'semantic-ui-react';

import { RequestParameterNames } from '../../constants/requestparameters';
import { defaultSortDirection, sortDirectionOptions } from '../../constants/sort';
import { search } from '../../store/actions/search';
import { getItems, getSortDirection } from '../../store/reducers/search';

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
    shouldRender: (getItems(state) &&  getItems(state).length),
    sortDirection: getSortDirection(state)
  };
}

/**
 * Sort direction component.
 */
class SortDirection extends React.Component {


  constructor(props) {

    super(props);

    // Icon

    let sortDirection = this.props.sortDirection ? this.props.sortDirection : defaultSortDirection;
    let icon = sortDirection == 'desc' ? 'sort amount down' : 'sort amount up';

    this.state = {
      icon: icon
    }

    // Bind functions to this instance.

    this.handleItemSelect = this.handleItemSelect.bind(this);
  }

  /**
   * Handle item selection event.
   * 
   * @param {Object} event 
   * @param {String} value 
   */
  handleItemSelect(event, { value }) {

    this.props.getSearchResults({ [RequestParameterNames.SORT_DIRECTION]: value })

    let icon = this.state.icon === 'sort amount down' ? 'sort amount up' : 'sort amount down';

    this.setState({ 'icon': icon });
  }

  /**
   * Render.
   */
  render() {

    const { icon } = this.state;
    const { shouldRender, SortDirection } = this.props;

    if (!shouldRender) {
      return null;
    }

    return (
      <Dropdown
        className='sort-direction'
        floating
        header={Liferay.Language.get('sort-direction')}
        icon={icon}
        defaultValue={defaultSortDirection}
        onChange={this.handleItemSelect}
        options={sortDirectionOptions}
        value={SortDirection}
      />
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(SortDirection);
