package com.enonic.wem.core.account.dao;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

public class AccountJcrMapping
{
    private static final String CREATED = "created";

    private static final String DISPLAY_NAME = "displayName";

    private static final String EMAIL = "email";

    private static final String LAST_MODIFIED = "lastModified";

    private static final String LAST_LOGGED = "lastLogged";

    private static final String PHOTO = "photo";

    private static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String DESCRIPTION = "description";

    private static final String BIRTHDAY = "birthday";

    private static final String COUNTRY = "country";

    private static final String FAX = "fax";

    private static final String FIRST_NAME = "firstName";

    private static final String GLOBAL_POSITION = "globalPosition";

    private static final String HOME_PAGE = "homePage";

    private static final String HTML_EMAIL = "htmlEmail";

    private static final String INITIALS = "initials";

    private static final String LAST_NAME = "lastName";

    private static final String LOCALE = "locale";

    private static final String MEMBER_ID = "memberId";

    private static final String MIDDLE_NAME = "middleName";

    private static final String MOBILE = "mobile";

    private static final String NICK_NAME = "nickName";

    private static final String ORGANIZATION = "organization";

    private static final String PERSONAL_ID = "personalId";

    private static final String PHONE = "phone";

    private static final String PREFIX = "prefix";

    private static final String SUFFIX = "suffix";

    private static final String TIMEZONE = "timeZone";

    private static final String TITLE = "title";

    private static final String GENDER = "gender";

    private static final String ADDRESSES = "addresses";

    private static final String ADDRESS = "address";

    private static final String ISO_COUNTRY = "isoCountry";

    private static final String ISO_REGION = "isoRegion";

    private static final String LABEL = "label";

    private static final String POSTAL_ADDRESS = "postalAddress";

    private static final String POSTAL_CODE = "postalCode";

    private static final String REGION = "region";

    private static final String STREET = "street";

    public UserAccount toUser( final Node userNode )
        throws RepositoryException, IOException
    {
        final String userName = userNode.getName();
        final String userStoreName = userNode.getParent().getParent().getName();
        final UserAccount user = UserAccount.create( userStoreName + ":" + userName );
        user.setDisplayName( JcrHelper.getPropertyString( userNode, DISPLAY_NAME ) );
        user.setCreatedTime( JcrHelper.getPropertyDateTime( userNode, CREATED ) );
        user.setEmail( userNode.getProperty( EMAIL ).getString() );
        user.setModifiedTime( JcrHelper.getPropertyDateTime( userNode, LAST_MODIFIED ) );
        user.setLastLoginTime( JcrHelper.getPropertyDateTime( userNode, LAST_LOGGED ) );

        if ( userNode.hasProperty( PHOTO ) )
        {
            user.setImage( JcrHelper.getPropertyBinary( userNode, PHOTO ) );
        }
        final UserProfile profile = toUserProfile( userNode );
        final List<Address> addresses = nodePropertiesToAddresses( userNode );
        profile.setAddresses( Addresses.from( addresses ) );
        user.setProfile( profile );
        return user;
    }

    public void userToJcr( final UserAccount user, final Node node )
        throws RepositoryException
    {
        node.setProperty( NAME, user.getKey().getLocalName() );
        node.setProperty( DISPLAY_NAME, user.getDisplayName() );
        JcrHelper.setPropertyDateTime( node, CREATED, user.getCreatedTime() );
        node.setProperty( EMAIL, user.getEmail() );
        JcrHelper.setPropertyDateTime( node, LAST_MODIFIED, user.getModifiedTime() );
        JcrHelper.setPropertyDateTime( node, LAST_LOGGED, user.getLastLoginTime() );
        if ( user.getImage() != null )
        {
            JcrHelper.setPropertyBinary( node, PHOTO, user.getImage() );
        }
        node.setProperty( TYPE, AccountType.USER.name() );
        userProfileToJcr( user.getProfile(), node.getNode( JcrConstants.USER_PROFILE_NODE ) );
    }

    public RoleAccount toRole( final Node roleNode )
        throws RepositoryException
    {
        final String roleName = roleNode.getName();
        final String userStoreName = roleNode.getParent().getParent().getName();
        final RoleAccount role = RoleAccount.create( userStoreName + ":" + roleName );
        role.setDisplayName( JcrHelper.getPropertyString( roleNode, DISPLAY_NAME ) );
        role.setCreatedTime( JcrHelper.getPropertyDateTime( roleNode, CREATED ) );
        role.setModifiedTime( JcrHelper.getPropertyDateTime( roleNode, LAST_MODIFIED ) );
        return role;
    }

    public void roleToJcr( final RoleAccount role, final Node node )
        throws RepositoryException
    {
        groupPropertiesToJcr( role, node );
        node.setProperty( TYPE, AccountType.ROLE.name() );
    }

    public GroupAccount toGroup( final Node groupNode )
        throws RepositoryException
    {
        final String groupName = groupNode.getName();
        final String userStoreName = groupNode.getParent().getParent().getName();
        final GroupAccount group = GroupAccount.create( userStoreName + ":" + groupName );
        group.setDisplayName( JcrHelper.getPropertyString( groupNode, DISPLAY_NAME ) );
        group.setCreatedTime( JcrHelper.getPropertyDateTime( groupNode, CREATED ) );
        group.setModifiedTime( JcrHelper.getPropertyDateTime( groupNode, LAST_MODIFIED ) );
        return group;
    }

    public void groupToJcr( final GroupAccount group, final Node node )
        throws RepositoryException
    {
        groupPropertiesToJcr( group, node );
        node.setProperty( TYPE, AccountType.GROUP.name() );
    }

    private void groupPropertiesToJcr( final NonUserAccount nonUserAccount, final Node node )
        throws RepositoryException
    {
        node.setProperty( NAME, nonUserAccount.getKey().getLocalName() );
        node.setProperty( DISPLAY_NAME, nonUserAccount.getDisplayName() );
        JcrHelper.setPropertyDateTime( node, LAST_MODIFIED, nonUserAccount.getModifiedTime() );
        JcrHelper.setPropertyDateTime( node, CREATED, nonUserAccount.getCreatedTime() );
    }

    private void userProfileToJcr( final UserProfile profile, final Node userNode )
        throws RepositoryException
    {
        JcrHelper.setPropertyDateTime( userNode, BIRTHDAY, profile.getBirthday() );
        userNode.setProperty( COUNTRY, profile.getCountry() );
        userNode.setProperty( DESCRIPTION, profile.getDescription() );
        userNode.setProperty( FAX, profile.getFax() );
        userNode.setProperty( FIRST_NAME, profile.getFirstName() );
        userNode.setProperty( GLOBAL_POSITION, profile.getGlobalPosition() );
        userNode.setProperty( HOME_PAGE, profile.getHomePage() );
        if ( profile.getHtmlEmail() != null )
        {
            userNode.setProperty( HTML_EMAIL, profile.getHtmlEmail() );
        }
        userNode.setProperty( INITIALS, profile.getInitials() );
        userNode.setProperty( LAST_NAME, profile.getLastName() );
        userNode.setProperty( LOCALE, profile.getLocale() != null ? profile.getLocale().toString() : null );
        userNode.setProperty( MEMBER_ID, profile.getMemberId() );
        userNode.setProperty( MIDDLE_NAME, profile.getMiddleName() );
        userNode.setProperty( MOBILE, profile.getMobile() );
        userNode.setProperty( NICK_NAME, profile.getNickName() );
        userNode.setProperty( ORGANIZATION, profile.getOrganization() );
        userNode.setProperty( PERSONAL_ID, profile.getPersonalId() );
        userNode.setProperty( PHONE, profile.getPhone() );
        userNode.setProperty( PREFIX, profile.getPrefix() );
        userNode.setProperty( SUFFIX, profile.getSuffix() );
        userNode.setProperty( TIMEZONE, profile.getTimeZone() != null ? profile.getTimeZone().getID() : null );
        userNode.setProperty( TITLE, profile.getTitle() );
        Gender gender = profile.getGender();
        if ( gender != null )
        {
            userNode.setProperty( GENDER, gender.toString() );
        }
        userNode.setProperty( ORGANIZATION, profile.getOrganization() );

        final Addresses addresses = profile.getAddresses();
        if ( addresses != null )
        {
            final Node addressesNode = userNode.hasNode( ADDRESSES ) ? userNode.getNode( ADDRESSES ) : userNode.addNode( ADDRESSES );
            for ( Address address : addresses )
            {
                addAddressNode( address, addressesNode );
            }
        }
    }

    private void addAddressNode( final Address address, final Node addressesNode )
        throws RepositoryException
    {
        final Node addressNode = addressesNode.addNode( ADDRESS );
        addressNode.setProperty( COUNTRY, address.getCountry() );
        addressNode.setProperty( ISO_COUNTRY, address.getIsoCountry() );
        addressNode.setProperty( ISO_REGION, address.getIsoRegion() );
        addressNode.setProperty( LABEL, address.getLabel() );
        addressNode.setProperty( POSTAL_ADDRESS, address.getPostalAddress() );
        addressNode.setProperty( POSTAL_CODE, address.getPostalCode() );
        addressNode.setProperty( REGION, address.getRegion() );
        addressNode.setProperty( STREET, address.getStreet() );
    }

    private UserProfile toUserProfile( final Node userNode )
        throws RepositoryException
    {
        final UserProfile profile = new UserProfile();
        profile.setBirthday( JcrHelper.getPropertyDateTime( userNode, BIRTHDAY ) );
        profile.setCountry( JcrHelper.getPropertyString( userNode, COUNTRY ) );
        profile.setDescription( JcrHelper.getPropertyString( userNode, DESCRIPTION ) );
        profile.setFax( JcrHelper.getPropertyString( userNode, FAX ) );
        profile.setFirstName( JcrHelper.getPropertyString( userNode, FIRST_NAME ) );
        profile.setGlobalPosition( JcrHelper.getPropertyString( userNode, GLOBAL_POSITION ) );
        profile.setHomePage( JcrHelper.getPropertyString( userNode, HOME_PAGE ) );
        profile.setHtmlEmail( JcrHelper.getPropertyBoolean( userNode, HTML_EMAIL ) );
        profile.setInitials( JcrHelper.getPropertyString( userNode, INITIALS ) );
        profile.setLastName( JcrHelper.getPropertyString( userNode, LAST_NAME ) );
        final String localeId = JcrHelper.getPropertyString( userNode, LOCALE );
        profile.setLocale( localeId == null ? null : new Locale( localeId ) );
        profile.setMemberId( JcrHelper.getPropertyString( userNode, MEMBER_ID ) );
        profile.setMiddleName( JcrHelper.getPropertyString( userNode, MIDDLE_NAME ) );
        profile.setMobile( JcrHelper.getPropertyString( userNode, MOBILE ) );
        profile.setNickName( JcrHelper.getPropertyString( userNode, NICK_NAME ) );
        profile.setOrganization( JcrHelper.getPropertyString( userNode, ORGANIZATION ) );
        profile.setPersonalId( JcrHelper.getPropertyString( userNode, PERSONAL_ID ) );
        profile.setPhone( JcrHelper.getPropertyString( userNode, PHONE ) );
        profile.setPrefix( JcrHelper.getPropertyString( userNode, PREFIX ) );
        profile.setSuffix( JcrHelper.getPropertyString( userNode, SUFFIX ) );
        final String timeZoneId = JcrHelper.getPropertyString( userNode, TIMEZONE );
        profile.setTimeZone( timeZoneId == null ? null : TimeZone.getTimeZone( timeZoneId ) );
        profile.setTitle( JcrHelper.getPropertyString( userNode, TITLE ) );
        final String genderStr = JcrHelper.getPropertyString( userNode, GENDER );
        profile.setGender( genderStr == null ? null : Gender.valueOf( genderStr ) );
        return profile;
    }

    private List<Address> nodePropertiesToAddresses( final Node userNode )
        throws RepositoryException
    {
        final List<Address> addressList = Lists.newArrayList();
        final Node addresses = userNode.getNode( ADDRESSES );
        final NodeIterator addressNodeIt = addresses.getNodes( ADDRESS );
        while ( addressNodeIt.hasNext() )
        {
            Node addressNode = addressNodeIt.nextNode();
            final Address address = new Address();
            address.setLabel( JcrHelper.getPropertyString( addressNode, LABEL ) );
            address.setStreet( JcrHelper.getPropertyString( addressNode, STREET ) );
            address.setPostalAddress( JcrHelper.getPropertyString( addressNode, POSTAL_ADDRESS ) );
            address.setPostalCode( JcrHelper.getPropertyString( addressNode, POSTAL_CODE ) );
            address.setRegion( JcrHelper.getPropertyString( addressNode, REGION ) );
            address.setCountry( JcrHelper.getPropertyString( addressNode, COUNTRY ) );
            address.setIsoRegion( JcrHelper.getPropertyString( addressNode, ISO_REGION ) );
            address.setIsoCountry( JcrHelper.getPropertyString( addressNode, ISO_COUNTRY ) );
            addressList.add( address );
        }
        return addressList;
    }

}
