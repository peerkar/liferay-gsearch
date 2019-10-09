// Parameter name constants.

export const RequestParameterNames = {
    KEYWORDS: 'q',
    SCOPE: 'scope',
    PAGE: 'page',
    RESULT_LAYOUT: 'resultLayout',
    SORT_DIRECTION: 'sortDirection',
    SORT_FIELD: 'sortField',
    TIME: 'time',
    TIME_FROM: 'timeFrom',
    TIME_TO: 'timeTo',
};

// Changes on these parameters force others than 'persistedParameters' to clear.
// This is because otherwise we might end up into a deadlock
// Where there was for example a facet selection not existing
// in a result set for a new keyword.

export const clearingParameters = [
    RequestParameterNames.KEYWORDS,
    RequestParameterNames.SCOPE,
    RequestParameterNames.TIME

];

export const persistedParameters = [
    RequestParameterNames.KEYWORDS,
    RequestParameterNames.RESULT_LAYOUT,
    RequestParameterNames.SCOPE
];