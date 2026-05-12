// Frida script: hook Aweme model for debugging URLs and ads
Java.perform(function() {
    var Aweme = Java.use("com.ss.android.ugc.aweme.feed.model.Aweme");

    Aweme.getVideo.implementation = function() {
        var video = this.getVideo();
        if (video != null) {
            var playAddr = video.getPlayAddr();
            if (playAddr != null) {
                console.log("[+] Video URLs: " + playAddr.getUrlList().value);
            }
        }
        return video;
    };

    Aweme.isAd.implementation = function() {
        var result = this.isAd();
        console.log("[+] isAd = " + result);
        return false; // FEATURE_HIDE_ADS test
    };

    Aweme.getDesc.implementation = function() {
        var desc = this.getDesc();
        console.log("[+] Desc: " + desc);
        return desc;
    };

    console.log("[*] Aweme hooks installed");
});
