package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public class UnpublishContentParams
{
    private final ContentIds contentIds;

    private final Branch unpublishBranch;

    private UnpublishContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        unpublishBranch = builder.unpublishBranch;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getUnpublishBranch()
    {
        return unpublishBranch;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch unpublishBranch;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder unpublishBranch( final Branch val )
        {
            unpublishBranch = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentIds, "contentId must be set" );
            Preconditions.checkNotNull( unpublishBranch, "unpublish-branch must be set" );
        }

        public UnpublishContentParams build()
        {
            this.validate();
            return new UnpublishContentParams( this );
        }
    }
}
