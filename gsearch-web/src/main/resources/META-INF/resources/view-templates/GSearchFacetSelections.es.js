import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import templates from './GSearchFacetSelections.soy';

/**
 * GSearch facet selections component.
 */
class GSearchFacetSelections extends Component {}

// Register component

Soy.register(GSearchFacetSelections, templates);

export default GSearchFacetSelections;	
