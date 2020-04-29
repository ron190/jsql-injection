#/bin/bash

echo "Restore old display :${OLD_DISPLAY}"

export DISPLAY=${OLD_DISPLAY}

echo "Kill test display :${NEW_DISPLAY}"

vncserver -kill ":${NEW_DISPLAY}"