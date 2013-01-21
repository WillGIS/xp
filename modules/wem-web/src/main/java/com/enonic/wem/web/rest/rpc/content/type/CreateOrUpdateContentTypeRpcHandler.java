package com.enonic.wem.web.rest.rpc.content.type;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.editor.ContentTypeEditors.setContentType;

@Component
public class CreateOrUpdateContentTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private final static Set<String> VALID_ICON_MIME_TYPES =
        ImmutableSet.of( "image/gif", "image/jpeg", "image/png", "image/tiff", "image/bmp" );

    private static final int MAX_ICON_SIZE = 512;

    private final ContentTypeXmlSerializer contentTypeXmlSerializer;

    private UploadService uploadService;

    public CreateOrUpdateContentTypeRpcHandler()
    {
        super( "contentType_createOrUpdate" );
        this.contentTypeXmlSerializer = new ContentTypeXmlSerializer();
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String contentTypeXml = context.param( "contentType" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        ContentType contentType;
        try
        {
            contentType = contentTypeXmlSerializer.toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid content type format" ) );
            return;
        }

        final UploadItem uploadItem = uploadService.getItem( iconReference );
        final byte[] icon = uploadItem != null ? getUploadedImage( uploadItem ) : null;
        if ( icon != null )
        {
            if ( !isValidImage( uploadItem, icon, context ) )
            {
                return;
            }
            contentType = newContentType( contentType ).icon( icon ).build();
        }

        if ( !contentTypeExists( contentType.getQualifiedName() ) )
        {
            final CreateContentType createContentType = contentType().create().contentType( contentType );
            client.execute( createContentType );
            context.setResult( CreateOrUpdateContentTypeJsonResult.created() );
        }
        else
        {
            final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( contentType.getQualifiedName() );
            final UpdateContentTypes updateContentType = contentType().update().names( names ).editor( setContentType( contentType ) );
            client.execute( updateContentType );
            context.setResult( CreateOrUpdateContentTypeJsonResult.updated() );
        }
    }

    private boolean isValidImage( final UploadItem uploadItem, final byte[] icon, final JsonRpcContext context )
        throws IOException
    {
        final String mimeType = uploadItem.getMimeType();
        if ( !isValidIconMimeType( mimeType ) )
        {
            context.setResult( new JsonErrorResult( "Unsupported image type: {0}", mimeType ) );
            return false;
        }

        final BufferedImage image = ImageIO.read( new ByteArrayInputStream( icon ) );
        if ( image == null )
        {
            context.setResult( new JsonErrorResult( "Unable to read image file" ) );
            return false;
        }
        if ( image.getWidth() > MAX_ICON_SIZE || image.getHeight() > MAX_ICON_SIZE )
        {
            context.setResult( new JsonErrorResult( "Icon size too big: {0}x{1} (maximum size " + MAX_ICON_SIZE + "x" + MAX_ICON_SIZE + ")",
                                                    image.getWidth(), image.getHeight() ) );
            return false;
        }
        return true;
    }

    private byte[] getUploadedImage( final UploadItem uploadItem )
        throws IOException
    {
        if ( uploadItem != null )
        {
            final File file = uploadItem.getFile();
            if ( file.exists() )
            {
                return FileUtils.readFileToByteArray( file );
            }
        }
        return null;
    }

    private boolean isValidIconMimeType( final String mimeType )
    {
        return ( mimeType != null ) && VALID_ICON_MIME_TYPES.contains( mimeType.toLowerCase() );
    }

    private boolean contentTypeExists( final QualifiedContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().names( QualifiedContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
