package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;

final class RequireFunction
    extends BaseFunction
{
    private final static String NAME = "require";

    private final static String EXPORTS_NAME = "exports";

    private final Map<ResourceKey, Scriptable> exportedInterfaces;

    private final Scriptable nativeScope;

    private final ScriptCompiler compiler;

    public RequireFunction( final Scriptable nativeScope, final ScriptCompiler compiler )
    {
        this.nativeScope = nativeScope;
        this.compiler = compiler;
        this.exportedInterfaces = Maps.newConcurrentMap();
        setPrototype( ScriptableObject.getFunctionPrototype( nativeScope ) );
    }

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public Object call( final Context context, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < 1 ) )
        {
            throw ScriptRuntime.throwError( context, scope, NAME + "() needs one argument" );
        }

        final RequireModuleScope moduleScope = (RequireModuleScope) thisObj;
        final String name = (String) Context.jsToJava( args[0], String.class );

        final ResourceKey resource = moduleScope.resolveScript( name );
        return getExportedInterface( context, resource );
    }

    public Scriptable requireMain( final Context context, final ResourceKey key )
    {
        return getExportedInterface( context, key );
    }

    private Scriptable getExportedInterface( final Context context, final ResourceKey key )
    {
        Scriptable exports = this.exportedInterfaces.get( key );
        if ( exports != null )
        {
            return exports;
        }

        exports = context.newObject( this.nativeScope );

        final Script script = this.compiler.compile( context, key );
        final Scriptable newExports = executeScript( context, exports, key, script );

        if ( exports != newExports )
        {
            exports = newExports;
        }

        return exports;
    }

    private Scriptable executeScript( final Context context, final Scriptable exports, final ResourceKey resource, final Script script )
    {
        final ScriptableObject moduleObject = (ScriptableObject) context.newObject( this.nativeScope );

        final Scriptable executionScope = new RequireModuleScope( this.nativeScope, resource );
        executionScope.put( EXPORTS_NAME, executionScope, exports );
        moduleObject.put( EXPORTS_NAME, moduleObject, exports );

        install( executionScope );

        script.exec( context, executionScope );
        return ScriptRuntime.toObject( this.nativeScope, ScriptableObject.getProperty( moduleObject, EXPORTS_NAME ) );
    }

    public RequireFunction install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, NAME, this );
        return this;
    }
}
