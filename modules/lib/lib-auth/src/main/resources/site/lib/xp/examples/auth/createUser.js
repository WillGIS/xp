var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Creates a user.
var user = authLib.createUser({
    userStore: 'myUserStore',
    name: 'userName',
    displayName: 'User display name',
    email: 'userName@enonic.com'
});
// END

// BEGIN
// Information when creating a user.
var expected = {
    "type": "user",
    "key": "user:enonic:user1",
    "displayName": "User 1",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "disabled": false,
    "email": "user1@enonic.com",
    "login": "user1",
    "userStore": "enonic"
};
// END

t.assertJsonEquals(expected, user);
