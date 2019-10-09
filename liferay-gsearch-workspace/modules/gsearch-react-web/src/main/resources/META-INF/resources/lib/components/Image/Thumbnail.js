import React from 'react';

class Thumbnail extends React.Component {

    constructor(props) {

        super(props);

        this.state = {
            isPortrait: false
        }

        this.onLoad = this.onLoad.bind(this);
    }

    /**
     * Checks whether the images is a portrait or landscape.
     * 
     * @param {Object} img 
     */
    isPortrait(img) {
        const w = img.naturalWidth || img.width,
            h = img.naturalHeight || img.height;
        return (h > w);
    }

    /**
     * Handles onload event.
     */
    onLoad() {

        let isPortrait = this.isPortrait(event.target);

        if (isPortrait) {
            this.setState({ isPortrait: true });
        }
    }

    /**
     * Render
     */
    render() {

        const { isPortrait } = this.state;
        const { alt, src } = this.props;

        if (!src) {
            return '';
        }

        return (
            <div className={"image gsearch-thumbnail " + (isPortrait ? "portrait" : "")}>
                <img alt={alt} onLoad={this.onLoad} src={src} />
            </div>
        );
    }
}

export default Thumbnail;