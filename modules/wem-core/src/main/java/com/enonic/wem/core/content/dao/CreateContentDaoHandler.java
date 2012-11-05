package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.PathNotFoundException;


class CreateContentDaoHandler
    extends AbstractContentDaoHandler
{
    CreateContentDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final Content content )
        throws RepositoryException
    {
        final Node root = session.getRootNode();
        final Node contentsNode = root.getNode( ContentDaoConstants.CONTENTS_PATH );
        final ContentPath path = content.getPath();

        if ( path.elementCount() == 1 )
        {
            if ( contentsNode.hasNode( path.getName() ) )
            {
                throw new IllegalArgumentException( "Content already exists: " + path );
            }
            addContentToJcr( content, contentsNode );
        }
        else
        {
            final Node parentContentNode = doGetContentNode( session, path.getParentPath() );
            if ( parentContentNode == null )
            {
                throw new PathNotFoundException( path.getParentPath() );
            }
            addContentToJcr( content, parentContentNode );
        }
    }

    void addContentToJcr( final Content content, final Node parentContentNode )
        throws RepositoryException
    {
        final Node newContentNode = parentContentNode.addNode( content.getName() );
        contentJcrMapper.toJcr( content, newContentNode );
    }
}
