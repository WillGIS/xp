package com.enonic.xp.core.impl.app;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;

public class ApplicationRepoServiceImplTest
{
    private NodeService nodeService = Mockito.mock( NodeService.class );

    private IndexService indexService = Mockito.mock( IndexService.class );

    private ApplicationRepoServiceImpl service;

    private URL rootTestUrl;

    @Before
    public void setUp()
        throws Exception
    {
        this.rootTestUrl = new File( "./src/test/resources" ).toURI().toURL();

        this.service = new ApplicationRepoServiceImpl();
        this.service.setIndexService( this.indexService );
        this.service.setNodeService( this.nodeService );
    }

    @Test
    public void create_node()
        throws Exception
    {
        final MockApplication app = createApp();

        this.service.createApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).create( Mockito.isA( CreateNodeParams.class ) );
    }

    @Test
    public void update_node()
        throws Exception
    {
        final MockApplication app = createApp();

        Mockito.when( this.nodeService.getByPath( NodePath.create( ApplicationRepoServiceImpl.APPLICATION_PATH, "myBundle" ).build() ) ).
            thenReturn( Node.create().
                id( new NodeId() ).
                name( "myBundle" ).
                parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).
                build() );

        this.service.updateApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    private MockApplication createApp()
    {
        final MockApplication app = new MockApplication();
        app.setKey( ApplicationKey.from( "myBundle" ) );
        app.setStarted( true );
        app.setUrlResolver( rootTestUrl, "/myApp" );
        return app;
    }
}