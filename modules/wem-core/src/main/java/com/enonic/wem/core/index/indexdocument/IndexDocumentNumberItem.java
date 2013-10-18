package com.enonic.wem.core.index.indexdocument;

public class IndexDocumentNumberItem
    extends AbstractIndexDocumentItem<Double>
{
    private final Double value;

    public IndexDocumentNumberItem( final String fieldName, final Double value )
    {
        super( fieldName );
        this.value = value;
    }

    @Override
    public IndexBaseType getIndexBaseType()
    {
        return IndexBaseType.NUMBER;
    }

    @Override
    public Double getValue()
    {
        return value;
    }
}
