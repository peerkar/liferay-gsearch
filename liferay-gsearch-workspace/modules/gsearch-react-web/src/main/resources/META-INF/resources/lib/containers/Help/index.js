import React from 'react'

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux'

import { Button, Header, Icon, Modal } from 'semantic-ui-react'

import { help } from "../../store/actions/help";
import { getHelpError, getHelpText, isHelpLoading } from '../../store/reducers/help';

// Modal opening effect.

const dimmerEffect = 'blurring';

/**
 * Redux mapping.
 *
 * @param {Object} dispatch 
 */
function mapDispatchToProps(dispatch) {
  
  const getHelp = help.request;

  return bindActionCreators({ getHelp }, dispatch);
}

/**
 * Redux mapping.
 *
 * @param {Object} state 
 */
function mapStateToProps(state, props) {
  return {
    error: getHelpError(state),
    isLoading: isHelpLoading(state),
    text: getHelpText(state)
  };
}

/**
 * Help component.
 */
class Help extends React.Component {

  /**
   * Constructor.
   * 
   * @param {Object} props 
   */
  constructor(props) {

    super(props);

    this.state = {
      open: false,
      text: ''
    }

    // Bind functions to this instance.

    this.close = this.close.bind(this);
    this.open = this.open.bind(this);
  }

  /**
   * Handles dialog close event.
   */
  close() {
    this.setState({ open: false });
  }

  /**
   * Handles dialog open event.
   */
  open() {

    if (this.props.text.length === 0) {
      this.props.getHelp();
    }
    this.setState({ open: true });
  }

  /**
   * Render.
   */
  render() {

    const { open } = this.state;
    const { isLoading, text } = this.props;

    return (

      <div className="gsearch-help-container">

        <a className="help-trigger" onClick={this.open}>{Liferay.Language.get('help')}</a>

        <Modal dimmer={dimmerEffect} open={open} onClose={this.close}>

          <Header icon='help' content={Liferay.Language.get('help')} />

          <Modal.Content scrolling>
            <div className={`gsearch-loading-wrapper ${isLoading ? '' : 'hide'}`}>
              <Icon loading name='spinner' />
            </div>
            <div className="gsearch-help-content" dangerouslySetInnerHTML={{ __html: text }}></div>
          </Modal.Content>

          <Modal.Actions>
            <Button onClick={this.close}>
              <Icon name='close' />{Liferay.Language.get('close')}
            </Button>
          </Modal.Actions>
        </Modal>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Help);
