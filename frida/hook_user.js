// Frida script: hook User model for fake changes debug
Java.perform(function() {
    var User = Java.use("com.ss.android.ugc.aweme.profile.model.User");

    User.isVerified.implementation = function() {
        console.log("[+] isVerified hooked -> true");
        return true;
    };

    User.getFollowerCount.implementation = function() {
        console.log("[+] getFollowerCount hooked -> 999999");
        return 999999;
    };

    User.getFollowingCount.implementation = function() {
        console.log("[+] getFollowingCount hooked -> 999999");
        return 999999;
    };

    console.log("[*] User hooks installed");
});
