package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemNgram
    extends IndexItem<IndexValueString>
{

    public IndexItemNgram( final IndexPath indexPath, final String value )
    {
        super( indexPath, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.NGRAM;
    }
}
