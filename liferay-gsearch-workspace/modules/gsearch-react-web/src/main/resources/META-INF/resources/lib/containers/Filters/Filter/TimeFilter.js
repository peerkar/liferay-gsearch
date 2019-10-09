import React from 'react'
import config from 'react-global-configuration';

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { DateInput } from 'semantic-ui-calendar-react';
import { Dropdown } from 'semantic-ui-react'

import { ConfigKeys } from '../../../constants/configkeys';
import { RequestParameterNames } from '../../../constants/requestparameters';
import { rangeParameterName, timeOptions } from '../../../constants/time';
import { search } from "../../../store/actions/search";
import { getTime, getTimeFrom, getTimeTo } from '../../../store/reducers/search';

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
function mapStateToProps(state) {
	return {
		time: (getTime(state) ? getTime(state) : timeOptions[0].value),
		timeFrom: (getTimeFrom(state) ? getTimeFrom(state) : ''),
		timeTo: (getTimeTo(state) ? getTimeTo(state) : '')
	};
}

/**
 * Time filter component.
 */
class TimeFilter extends React.Component {

    constructor(props) {

        super(props);

        // Resolve text for initial value

        let text = null;

        if (this.props.time === rangeParameterName) {
            text = Liferay.Language.get('time-range');
        } else {
            let option = timeOptions.find(option => option.value === this.props.time);
            if (option) {
                text = option.text;
            }
        }
        if (text === null) {
            text = timeOptions[0].text;
        }

        this.state = {
            showTimeFromDatePicker: false,
            showTimeToDatePicker: false,
            text: text
        }

        // Bind functions to this instance.

        this.handleChange = this.handleChange.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleItemClick = this.handleItemClick.bind(this);
        this.handleDateChange = this.handleDateChange.bind(this);
        this.toggleDatePicker = this.toggleDatePicker.bind(this);

        this.dropdownRef = React.createRef();

        // Component configuration.

        this.filterConfig = config.get(ConfigKeys.FILTER);
    }

    /**
     * Handle filter change event.
     * 
     * @param {Object} event
     * @param {String} value 
     */
    handleChange(event, { value }) {
        
        this.setState(
            {
                text: event.target.textContent,
            }
        );

        // Do search.

        this.props.getSearchResults({
            [RequestParameterNames.TIME]: value,
            [RequestParameterNames.TIME_FROM]: '',
            [RequestParameterNames.TIME_TO]: ''
        });
    }

    /**
     * Handle close event.
     * 
     * @param {Object} event
     * @param {String} value 
     */
    handleClose(event, { searchQuery }) {
        this.setState({ searchQuery: '' });
    }

    /**
     * Handle click event.
     * 
     * @param {Object} event
     * @param {String} value 
     */
    handleItemClick(event, data) {

        // Dispatch the item event to parent dropdown.

        this.dropdownRef.current.handleItemClick(event, data);
    }

    /**
     * Handle date change event.
     * 
     * @param {Object} event
     * @param {String} name 
     * @param {String} value 
     */
    handleDateChange(event, { name, value }) {

        let time = rangeParameterName;
        let timeFrom = this.props.timeFrom;
        let timeTo = this.props.timeTo;
        
        if (name === RequestParameterNames.TIME_FROM) {
            this.setState({ showTimeFromDatePicker: false })
            timeFrom = value;
        } else {
            this.setState({ showTimeToDatePicker: false })
            timeTo = value;
        }

        this.setState(
            {
                text: Liferay.Language.get('time-range'),
            }
        );

        // Do search.

        this.props.getSearchResults({
            [RequestParameterNames.TIME]: time,
            [RequestParameterNames.TIME_FROM]: timeFrom,
            [RequestParameterNames.TIME_TO]: timeTo
        })
    }

    /**
     * Toggle datepicker.
     * 
     * @param {Object} event 
     */
    toggleDatePicker(event) {

        const timeFrom = event.target.className.indexOf('time-from') > 0;

        if (timeFrom) {
            const newValue = this.state.showTimeFromDatePicker ? false : true;
            this.setState({ showTimeFromDatePicker: newValue })
        } else {
            const newValue = this.state.showTimeToDatePicker ? false : true;
            this.setState({ showTimeToDatePicker: newValue })
        }

        event.stopPropagation();
        event.nativeEvent.stopImmediatePropagation();
    }

    /**
     * Render
     */
    render() {

        const { time, timeFrom, timeTo } = this.props;
        const { showTimeFromDatePicker, showTimeToDatePicker, text } = this.state;

        return (
            <div className='gsearch-time-filter-wrapper'>

                <Dropdown
                    className='gsearch-filter time-filter'
                    compact
                    direction='left'
                    item
                    onChange={this.handleChange}
                    onClose={this.handleClose}
                    value={time}
                    ref={this.dropdownRef}
                    text={text}
                >
                    <Dropdown.Menu>
                        {timeOptions.map(option => (
                            <Dropdown.Item
                                active={option.value === time}
                                key={option.value}
                                {...option}
                                onClick={this.handleItemClick}
                            />
                        ))}

                        <Dropdown.Header icon='clock' content={Liferay.Language.get('time-range')} />
                        <Dropdown.Item 
                            className='time-from' 
                            onClick={this.toggleDatePicker}>
                                {Liferay.Language.get('time-from')} {timeFrom ? ': ' + timeFrom : ''}
                            </Dropdown.Item>
                            <Dropdown.Item 
                                className='time-to'
                                onClick={this.toggleDatePicker}>
                                    {Liferay.Language.get('time-to')} {timeTo ? ': ' + timeTo : ''}
                            </Dropdown.Item>
                    </Dropdown.Menu>
                </Dropdown>

                {showTimeFromDatePicker ?
                    <div className="gsearch-datepicker">
                        <DateInput
                            closable
                            dateFormat={this.filterConfig.datePickerDateFormat}
                            inline
                            name='timeFrom'
                            pickerWidth='300px'
                            value={timeFrom}
                            onChange={this.handleDateChange}
                        />
                    </div>
                    : null}

                {showTimeToDatePicker ?
                    <div className="gsearch-datepicker">
                        <DateInput
                            closable
                            dateFormat={this.filterConfig.datePickerDateFormat}
                            inline
                            name='timeTo'
                            value={timeTo}
                            onChange={this.handleDateChange}
                        />
                    </div>
                    : null}
            </div>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(TimeFilter);
