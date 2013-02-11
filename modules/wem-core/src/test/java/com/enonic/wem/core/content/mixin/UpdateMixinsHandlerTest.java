package com.enonic.wem.core.content.mixin;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.mixin.UpdateMixins;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.Mixins;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.mixin.dao.MixinDao;
import com.enonic.wem.core.time.MockTimeService;
import com.enonic.wem.core.time.TimeService;

import static com.enonic.wem.api.content.mixin.Mixin.newMixin;
import static com.enonic.wem.api.content.mixin.MixinEditors.setMixin;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateMixinsHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateMixinsHandler handler;

    private MixinDao mixinDao;

    private final DateTime CURRENT_TIME = DateTime.now();

    private final TimeService timeService = new MockTimeService( CURRENT_TIME );


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new UpdateMixinsHandler();
        handler.setMixinDao( mixinDao );
        handler.setTimeService( timeService );
    }

    @Test
    public void updateMixin()
        throws Exception
    {
        // setup
        final ModuleName module = ModuleName.from( "myModule" );
        final Mixin existingMixin = newMixin().
            displayName( "Age" ).
            module( module ).
            formItem( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).
            build();
        Mockito.when(
            mixinDao.select( Mockito.eq( QualifiedMixinNames.from( "myModule:age" ) ), Mockito.any( Session.class ) ) ).thenReturn(
            Mixins.from( existingMixin ) );

        final Mixins mixins = Mixins.from( existingMixin );
        Mockito.when( mixinDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        final FormItem formItemToSet = newInput().name( "age" ).type( InputTypes.WHOLE_NUMBER ).build();
        final UpdateMixins command =
            Commands.mixin().update().names( QualifiedMixinNames.from( "myModule:age" ) ).editor( setMixin( "age2", formItemToSet, null ) );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( mixinDao, atLeastOnce() ).update( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        assertEquals( (Integer) 1, command.getResult() );
    }

}
