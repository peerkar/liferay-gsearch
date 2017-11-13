import Autocomplete from 'metal-autocomplete/src/Autocomplete';

const DOWN = 40;
const ENTER = 13;
const UP = 38;

/*
 * GSearch autocomplete component extending Metal.JS autocomplete.
 */
class GSearchAutocomplete extends Autocomplete {

	/**
	 * This is an override of the original Metal.js Autocomplete.
	 * It simply removes SPACE from the suggestion select keys.
	 *
	 * @param {!Event} event
	 * @protected
	 */
	handleKeyDown_(event) {
		
		if (this.visible) {
			switch (event.keyCode) {
				case UP:
					this.activateListItem_(this.decreaseIndex_());
					event.preventDefault();
					break;
				case DOWN:
					this.activateListItem_(this.increaseIndex_());
					event.preventDefault();
					break;
				case ENTER:
					this.handleActionKeys_();
					event.preventDefault();
				break;
			}
		}
	}
}
export default GSearchAutocomplete;
