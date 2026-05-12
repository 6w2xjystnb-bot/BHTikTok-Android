// Frida script: enumerate TikTok classes
Java.perform(function() {
    Java.enumerateLoadedClasses({
        onMatch: function(className) {
            if (className.includes("com.ss.android.ugc.aweme") ||
                className.includes("com.zhiliaoapp")) {
                console.log(className);
            }
        },
        onComplete: function() {
            console.log("[*] Enumeration complete");
        }
    });
});
