package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.exception.ContentAlreadyExistException;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.core.content.ContentPathNameGenerator;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.content.editor.ContentEditors.composite;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentData;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentDisplayName;

@Component
public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{
    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( context.param( "contentPath" ).required().asString() );

        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final String displayName = context.param( "displayName" ).required().asString();
        if ( displayName.trim().isEmpty() )
        {
            context.setResult( new JsonErrorResult( "Parameter [displayName] is required" ) );
            return;
        }

        final ContentType contentType = getContentType( qualifiedContentTypeName );

        final ContentData contentData = new ContentDataParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

        if ( !contentExists( contentPath ) )
        {
            try
            {
                final ContentId contentId = doCreateContent( qualifiedContentTypeName, contentPath, displayName, contentData );
                context.setResult( CreateOrUpdateContentJsonResult.created( contentId ) );
            }
            catch ( ContentNotFoundException e )
            {
                context.setResult(
                    new JsonErrorResult( "Unable to create content. Path [{0}] does not exist", contentPath.getParentPath().toString() ) );
            }
            catch ( ContentAlreadyExistException e )
            {
                context.setResult( new JsonErrorResult( "Content with path [{0}] already exists.", contentPath.toString() ) );
            }
        }
        else
        {
            doUpdateContent( contentPath, displayName, contentData );
            context.setResult( CreateOrUpdateContentJsonResult.updated() );
        }
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        return client.execute( getContentTypes ).first();
    }

    private ContentId doCreateContent( final QualifiedContentTypeName qualifiedContentTypeName, ContentPath contentPath,
                                       final String displayName, final ContentData contentData )
    {
        final ContentPathNameGenerator contentPathNameGenerator = new ContentPathNameGenerator();
        ContentPath parentPath = contentPath.getParentPath();
        if ( parentPath == null )
        {
            parentPath = ContentPath.ROOT;
        }
        contentPath = ContentPath.from( parentPath, contentPathNameGenerator.generatePathName( displayName ) );

        final CreateContent createContent = Commands.content().create();
        createContent.contentPath( contentPath );
        createContent.contentType( qualifiedContentTypeName );
        createContent.contentData( contentData );
        createContent.displayName( displayName );
        createContent.owner( AccountKey.anonymous() );
        return client.execute( createContent );
    }

    private void doUpdateContent( final ContentPath contentPath, final String displayName, final ContentData contentData )
    {
        final UpdateContents updateContents = Commands.content().update();
        updateContents.selectors( ContentPaths.from( contentPath ) );
        updateContents.editor( composite( setContentData( contentData ), setContentDisplayName( displayName ) ) );
        updateContents.modifier( AccountKey.anonymous() );

        client.execute( updateContents );
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( Commands.content().get().selectors( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }
}
