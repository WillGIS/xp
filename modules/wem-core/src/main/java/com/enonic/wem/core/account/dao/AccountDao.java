package com.enonic.wem.core.account.dao;

import javax.jcr.Session;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.userstore.UserStore;

public interface AccountDao
    extends AccountDaoConstants
{
    public boolean delete( Session session, AccountKey key )
        throws Exception;

    public void createUserStore( Session session, UserStore userStore )
        throws Exception;

    public void createUser( final Session session, final UserAccount user )
        throws Exception;

    public void createGroup( Session session, GroupAccount group )
        throws Exception;

    public void createRole( Session session, RoleAccount role )
        throws Exception;

    public void setMembers( Session session, AccountKey nonUserAccount, AccountKeys members )
        throws Exception;
}
