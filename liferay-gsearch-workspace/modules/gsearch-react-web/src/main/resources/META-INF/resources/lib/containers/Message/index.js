import React, { createRef, Fragment } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux'
import { Popup } from 'semantic-ui-react';

import { getMessage } from '../../store/reducers/message';
import { setMessage } from '../../store/actions/message';

const messageTimeout = 2500;

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state) {
    return {
        message: getMessage(state)
    };
}

/**
 * Redux mapping.
 *
 * @param {Object} dispatch 
 */
function mapDispatchToProps(dispatch) {
   return bindActionCreators({ setMessage }, dispatch);
}

class Message extends React.Component {

    constructor(props) {

        super(props);

        this.contextRef = createRef();
    }

    render() {

        const { message } = this.props;
        
        if (!message) {
        	return null;
        } else {
    		setTimeout(()=> {
    			this.props.setMessage(null);
    		}, messageTimeout);
        }

        return (
            <Fragment>
            	<span className="message" ref={this.contextRef}></span>
                <Popup
                    content={message}
                    context={this.contextRef}
                    open
                    position='top left'
                />
            </Fragment>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Message);
