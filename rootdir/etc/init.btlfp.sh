#!/vendor/bin/sh

fwfile="/data/system/users/0/settings_fingerprint.xml"
hwdir="/mnt/vendor/persist/data/fingerprint"

logmsg() {
    /vendor/bin/log -t "fingerprint" "$@"
}

# If the framework fingerprint data does not exist, ensure that the
# hardware data is also deleted.
if [ ! -f "$fwfile" ]; then
    logmsg "$fwfile does not exist"
    if [ -d "$hwdir" ]; then
        logmsg "$hwdir exists, deleting"
        rm -rf "$hwdir"
    fi
fi
