package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.type.GetContentTypes;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.ContentTypes;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.type.validator.DataValidationErrors;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.command.Commands.contentType;

@Component
public final class ValidateContentDataRpcHandler
    extends AbstractDataRpcHandler
{

    public ValidateContentDataRpcHandler()
    {
        super( "content_validate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String contentTypeParam = context.param( "qualifiedContentTypeName" ).required().asString();
        final ObjectNode contentDataParam = context.param( "contentData" ).required().asObject();

        final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( contentTypeParam );

        final ContentType contentType = getContentType( qualifiedContentTypeName );
        final RootDataSet rootDataSet = new RootDataSetParser( contentType ).parse( contentDataParam );

        final DataValidationErrors validationErrors =
            client.execute( content().validate().rootDataSet( rootDataSet ).contentType( contentType ) );

        final ValidateContentDataJsonResult result = new ValidateContentDataJsonResult( validationErrors );
        context.setResult( result );
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentType = contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentTypes contentTypes = client.execute( getContentType );
        return contentTypes.first();
    }
}
