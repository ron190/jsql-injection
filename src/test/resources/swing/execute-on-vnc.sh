#!/usr/bin/env sh

NEW_DISPLAY=42
DONE="no"

while [ "$DONE" == "no" ]
do
  out=$(xdpyinfo -display :${NEW_DISPLAY} 2>&1)
  if [[ "$out" == name* ]] || [[ "$out" == Invalid* ]]
  then
    # command succeeded; or failed with access error;  display exists
    (( NEW_DISPLAY+=1 ))
  else
    # display doesn't exist
    DONE="yes"
  fi
done

echo "Using first available display :${NEW_DISPLAY}"

mkdir $HOME/.vnc/
vncpasswd -f > $HOME/.vnc/passwd <<EOF
123456
123456
EOF

cat $HOME/.vnc/passwd

chmod 600 $HOME/.vnc/passwd

# mkdir /usr/X11R6/lib
# mkdir /usr/X11R6/lib/X11
# ln -s /usr/share/X11/fonts /usr/X11R6/lib/X11/fonts

xset q
 
OLD_DISPLAY=${DISPLAY}
vncserver ":${NEW_DISPLAY}" -localhost -geometry 800x600 -depth 16
export DISPLAY=:${NEW_DISPLAY}

echo hi!

"$@"

echo hi!

export DISPLAY=${OLD_DISPLAY}
vncserver -kill ":${NEW_DISPLAY}"

echo hi!