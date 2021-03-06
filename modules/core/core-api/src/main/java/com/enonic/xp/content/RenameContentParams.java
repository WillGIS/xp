package com.enonic.xp.content;


import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@Beta
public final class RenameContentParams
{
    private ContentId contentId;

    private ContentName newName;

    public RenameContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.newName = builder.newName;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentName getNewName()
    {
        return newName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof RenameContentParams ) )
        {
            return false;
        }

        final RenameContentParams that = (RenameContentParams) o;
        return Objects.equal( this.contentId, that.contentId ) && Objects.equal( this.newName, that.newName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId, this.newName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
        Preconditions.checkNotNull( this.newName, "name cannot be null" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId contentId;

        private ContentName newName;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder newName( final ContentName newName )
        {
            this.newName = newName;
            return this;
        }

        public RenameContentParams build()
        {
            return new RenameContentParams( this );
        }
    }
}
