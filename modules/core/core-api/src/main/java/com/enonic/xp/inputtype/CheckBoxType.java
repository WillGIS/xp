package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@Beta
final class CheckBoxType
    extends InputTypeBase
{
    public final static CheckBoxType INSTANCE = new CheckBoxType();

    private final static String VALID_VALUE = "checked";

    private CheckBoxType()
    {
        super( InputTypeName.CHECK_BOX );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            final String defaultValue = defaultProperty.getValue();

            return VALID_VALUE.equals( defaultValue ) ? ValueFactory.newBoolean(true) :
                super.createDefaultValue( defaultConfig );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }
}
