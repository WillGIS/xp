package com.enonic.wem.core.content

import com.enonic.wem.core.content.type.ContentType
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException
import com.enonic.wem.core.content.type.formitem.FormItemSet
import com.enonic.wem.core.content.type.formitem.VisualFieldSet
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes

import static com.enonic.wem.core.content.type.formitem.Component.newField

class ContentSpecTest extends spock.lang.Specification
{
    def "Given required Field not given Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        contentType.addFormItem( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required FieldSet not given Data in any of it's fields then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        FormItemSet fieldSet = FormItemSet.newFieldSet().name( "myFieldSet" ).required( true ).build();
        fieldSet.addField( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( false ).build() )
        contentType.addFormItem( fieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required FieldSet with a Data containing an empty value in it's one and only required text Field then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        FormItemSet fieldSet = FormItemSet.newFieldSet().name( "myFieldSet" ).required( true ).build();
        fieldSet.addField( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( true ).build() )
        contentType.addFormItem( fieldSet );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a FieldSet and the field has a Data containing and empty value then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        FormItemSet fieldSet = FormItemSet.newFieldSet().name( "myFieldSet" ).required( false ).build();
        fieldSet.addField( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( true ).build() )
        contentType.addFormItem( fieldSet );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a FieldSet and the field has no Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        FormItemSet fieldSet = FormItemSet.newFieldSet().name( "myFieldSet" ).required( false ).build();
        fieldSet.addField( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( true ).build() )
        contentType.addFormItem( fieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a VisualFieldSet and the field has no Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        VisualFieldSet visualFieldSet = VisualFieldSet.newVisualFieldSet().name( "myVisualFieldSet" ).label( "My VisualFieldSet" ).build();
        visualFieldSet.addFormItem( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).required( true ).build() )
        contentType.addFormItem( visualFieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

}
