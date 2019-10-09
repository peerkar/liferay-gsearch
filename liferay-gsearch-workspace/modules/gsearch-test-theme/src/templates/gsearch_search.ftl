<#assign VOID = freeMarkerPortletPreferences.setValue("portletSetupPortletDecoratorId", "barebone") />
<#assign VOID = freeMarkerPortletPreferences.setValue("showListed", "false") />
<#assign VOID = freeMarkerPortletPreferences.setValue("targetPortletId", "") />

<@liferay_portlet["runtime"]
    defaultPreferences="${freeMarkerPortletPreferences}"
    portletProviderAction=portletProviderAction.VIEW
    portletName="gsearchminiportlet"
/>
<#assign VOID = freeMarkerPortletPreferences.reset() />
