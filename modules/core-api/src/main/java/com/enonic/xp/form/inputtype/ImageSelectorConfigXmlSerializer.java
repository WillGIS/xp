package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.xml.DomHelper;

final class ImageSelectorConfigXmlSerializer
    implements AbstractInputTypeConfigXmlSerializer<ImageSelectorConfig>
{
    public static final ImageSelectorConfigXmlSerializer DEFAULT = new ImageSelectorConfigXmlSerializer();

    @Override
    public ImageSelectorConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( currentApplication );

        final ImageSelectorConfig.Builder builder = ImageSelectorConfig.create();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( resolver.toRelationshipTypeName( text ) );
        }

        return builder.build();
    }
}
