import React from 'react'
import { Message } from 'semantic-ui-react'

/**
 * Error message component.
 */
class Error extends React.Component {

  constructor(props) {

    super(props);

    this.state = {
      open: false
    }

    // Bind functions to this this instance.

    this.toggleDetails = this.toggleDetails.bind(this);
  }

  /**
   * Handles details click event.
   */
  toggleDetails() {

    this.setState({ open: (this.state.open ? false : true) });
  }

  /**
   * Render.
   */
  render() {

    const { open } = this.state
    const { error } = this.props

    return (
      <Message className="gsearch-error" negative>

        <Message.Header>{Liferay.Language.get('error-title')}</Message.Header>

        <p><a onClick={this.toggleDetails}>{Liferay.Language.get('toggle-details')}</a></p>

        <div className={open ? '' : 'hide'}>
          <p><strong>{Liferay.Language.get('error-message')}:</strong></p>
          <div>{error.message}</div>
          <p><strong>{Liferay.Language.get('error-details')}:</strong></p>
          <div>{error.stack}</div>
        </div>
      </Message>
    )
  }
}

export default Error;
