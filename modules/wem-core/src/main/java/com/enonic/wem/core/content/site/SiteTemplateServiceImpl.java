package com.enonic.wem.core.content.site;


import com.enonic.wem.api.content.site.CreateSiteTemplateParams;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParams;
import com.enonic.wem.core.config.SystemConfig;

public final class SiteTemplateServiceImpl
    implements SiteTemplateService
{
    private SystemConfig systemConfig;

    private SiteTemplateExporter siteTemplateExporter;

    @Override
    public SiteTemplates getSiteTemplates()
    {
        return new GetSiteTemplatesCommand().
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

    @Override
    public SiteTemplate createSiteTemplate( final CreateSiteTemplateParams param )
    {
        return new CreateSiteTemplateCommand().
            param( param ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

    @Override
    public SiteTemplate updateSiteTemplate( final UpdateSiteTemplateParams param )
        throws SiteTemplateNotFoundException
    {
        return new UpdateSiteTemplateCommand().
            param( param ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

    @Override
    public SiteTemplate getSiteTemplate( final SiteTemplateKey key )
        throws SiteTemplateNotFoundException
    {
        return new GetSiteTemplateCommand().
            key( key ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

    @Override
    public void deleteSiteTemplate( final SiteTemplateKey key )
        throws SiteTemplateNotFoundException
    {
        new DeleteSiteTemplateCommand().
            key( key ).
            systemConfig( this.systemConfig ).
            execute();
    }

    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    public void setSiteTemplateExporter( final SiteTemplateExporter siteTemplateExporter )
    {
        this.siteTemplateExporter = siteTemplateExporter;
    }
}
