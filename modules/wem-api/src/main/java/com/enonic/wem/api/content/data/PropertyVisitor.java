package com.enonic.wem.api.content.data;

import com.enonic.wem.api.content.data.type.ValueType;

public abstract class PropertyVisitor
{
    private ValueType valueType;

    public PropertyVisitor restrictType( ValueType valueType )
    {
        this.valueType = valueType;
        return this;
    }

    public void traverse( final Iterable<Data> entryIterable )
    {
        for ( Data data : entryIterable )
        {
            if ( data.isProperty() )
            {
                final Property property = data.toProperty();
                if ( valueType != null && !valueType.equals( property.getType() ) )
                {
                    continue;
                }
                visit( property );
            }
            else if ( data.isDataSet() )
            {
                traverse( data.toDataSet() );
            }
        }
    }

    public abstract void visit( Property property );
}
