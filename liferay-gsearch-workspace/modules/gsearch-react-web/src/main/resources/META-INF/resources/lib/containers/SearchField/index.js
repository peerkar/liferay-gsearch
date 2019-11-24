import React, { createRef } from 'react';
import Autosuggest from 'react-autosuggest';
import AutosuggestHighlightMatch from 'autosuggest-highlight/match';
import AutosuggestHighlightParse from 'autosuggest-highlight/parse';
import config from 'react-global-configuration';

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux'
import { Icon } from 'semantic-ui-react';

import { ConfigKeys } from '../../constants/configkeys';
import { RequestParameterNames } from '../../constants/requestparameters';
import { setMessage } from '../../store/actions/message';
import { search } from "../../store/actions/search";
import { suggestions } from "../../store/actions/suggestions";
import { getSuggestions, isSuggestionsLoading  } from '../../store/reducers/suggestions';
import { getKeywords } from '../../store/reducers/search';

/**
 * Gets single suggestion value.
 * 
 * @param {Object} suggestion 
 */
function getSuggestionValue(suggestion) {

  // We only have a string array here.

  return suggestion;
}

/**
 * Renders a single suggestion.
 * 
 * @param {Object} suggestion 
 * @param {String} query 
 */
function renderSuggestion(suggestion, { query }) {

  const suggestionText = suggestion;
  const matches = AutosuggestHighlightMatch(suggestionText, query);
  const parts = AutosuggestHighlightParse(suggestionText, matches);

  return (
    <span className='suggestion-content'>
      <span className='name'>
        {
          parts.map((part, index) => {
            const className = part.highlight ? 'gsearch-highlight' : null;

            return (
              <span className={className} key={index}>{part.text}</span>
            );
          })
        }
      </span>
    </span>
  );
}

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
  return {
    initialKeywords: getKeywords(state),
    isLoading: isSuggestionsLoading(state),
    results: getSuggestions(state)
  };
}

/**
 * Redux mapping.
 *
 * @param {Object} dispatch 
 */
function mapDispatchToProps(dispatch) {
  
  const getSuggestions = suggestions.request;
  const getSearchResults = search.request;

  return bindActionCreators({ getSearchResults, getSuggestions, setMessage }, dispatch);
}

/**
 * Searchfield component.
 * 
 * @see https://github.com/moroshko/react-autosuggest for options.
 */
class SearchField extends React.Component {

  constructor(props) {

    super(props);

    this.state = {
      value: (this.props.initialKeywords ? this.props.initialKeywords : '')
    }
    
    // Bind functions to this instance.

    this.onChange = this.onChange.bind(this);
    this.onKeyPress = this.onKeyPress.bind(this);
    this.onSuggestionsFetchRequested = this.onSuggestionsFetchRequested.bind(this);
    this.onSuggestionsClearRequested = this.onSuggestionsClearRequested.bind(this);
    this.onSuggestionSelected = this.onSuggestionSelected.bind(this);
    this.shouldRenderSuggestions = this.shouldRenderSuggestions.bind(this);

    // Variable acting as a suggestion request buffer.

    this.lastSuggestionRequestId = null;

    // Component configuration.

    this.searchFieldConfig = config.get(ConfigKeys.SEARCH_FIELD);
  }

  /**
   * Fires the search request.
   * 
   * @param {String} value 
   */
  doSearchRequest(value) { 

    // Check for minimum character count.

    if (value.trim().length >= this.searchFieldConfig.queryMinLength) {
		this.props.getSearchResults({ [RequestParameterNames.KEYWORDS]: value })
    } else {
		this.props.setMessage(Liferay.Language.get('min-character-count-is') + ' ' + this.searchFieldConfig.queryMinLength);
	}
  }

  /**
   * Handles searchfield content change event.
   * 
   * @param {Object} event 
   * @param {Object} data 
   */
  onChange(event, data) {
    this.setState({
      value: data.newValue
    });
  };

  /**
   * Handles searchfield keypress event.
   * 
   * @param {Object} event 
   * @param {String} value 
   */
  onKeyPress(event, value) {

    // Trigger search request on 'Enter'.
    // We have to get the fresh value from event.

    if (event.key === 'Enter') {
      this.doSearchRequest(event.target.value);
    }
  }

  /**
   * Handles searchfield clear request event.
   */
  onSuggestionsClearRequested() {
    // We'll let the Reduxed backend to take care of this.
  }

  /**
   * Handles suggestions fetch request.
   * 
   * @param {Object} data 
   */
  onSuggestionsFetchRequested(data) {

    // Cancel the previous request, if buffered.

    if (this.lastSuggestionRequestId !== null) {
      clearTimeout(this.lastSuggestionRequestId);
    }

    this.lastSuggestionRequestId = setTimeout(() => {
      this.props.getSuggestions(data.value);
    }, this.searchFieldConfig.keywordSuggesterRequestDelay);
  }

  /**
   * Handles suggestion select event.
   * 
   * @param {Object} event 
   * @param {String} suggestionValue 
   */
  onSuggestionSelected(event, { suggestionValue }) {
    this.doSearchRequest(suggestionValue);
  }

  /**
   * Decides whether to show suggestions.
   *
   * @param {String} value 
   */
  shouldRenderSuggestions(value) {

    // Don't proceed if suggestions are disabled by configuration.

    if (!this.searchFieldConfig.keywordSuggesterEnabled) {
      return false;
    }

    if (value.trim().length >= this.searchFieldConfig.queryMinLength) {
       return true;
    }

    return false;
  }

  /**
   * Render.
   */
  render() {
	  
    const { isLoading, results } = this.props;
    let { value } = this.state;

    // Props required by the Autosuggest component

    const inputProps = {
      onChange: this.onChange,
      onKeyPress: this.onKeyPress,
      placeholder: this.searchFieldConfig.keywordsPlaceholder,
      value
    };

    return (

      // Using Semantic UI CSS classes.

      <div className="ui fluid search gsearch-searchfield">

      	<div className="ui icon input">
          <Autosuggest
            getSuggestionValue={getSuggestionValue}
            inputProps={inputProps}
            suggestions={results}
            onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
            onSuggestionsClearRequested={this.onSuggestionsClearRequested}
            onSuggestionSelected={this.onSuggestionSelected}
            renderSuggestion={renderSuggestion}
            shouldRenderSuggestions={this.shouldRenderSuggestions}
          />
          <Icon className='icon-magnifier icon' name='search' loading={isLoading} />
        </div>
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(SearchField);
