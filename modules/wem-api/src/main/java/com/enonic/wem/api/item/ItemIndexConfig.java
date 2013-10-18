package com.enonic.wem.api.item;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;

public class ItemIndexConfig
{

    private final String analyzer;

    private final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs;

    public static Builder newItemIndexConfig()
    {
        return new Builder();
    }

    private ItemIndexConfig( final Builder builder )
    {

        this.analyzer = builder.analyzer;
        this.propertyIndexConfigs = builder.propertyIndexConfigs;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public PropertyIndexConfig getPropertyIndexConfig( final DataPath dataPath )
    {
        return propertyIndexConfigs.get( dataPath );
    }

    public static class Builder
    {
        private String analyzer;

        private final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = Maps.newHashMap();

        public Builder analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public Builder addPropertyIndexConfig( final Property property, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( property.getPath(), propertyIndexConfig );
            return this;
        }

        public Builder addPropertyIndexConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( DataPath.from( path ), propertyIndexConfig );
            return this;
        }

        public ItemIndexConfig build()
        {
            return new ItemIndexConfig( this );
        }

    }


}
