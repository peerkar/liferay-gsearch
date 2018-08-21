import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import templates from './GSearchStats.soy';

/**
 * GSearch sort component.
 */
class GSearchStats extends Component {}

// Register component

Soy.register(GSearchStats, templates);

export default GSearchStats;