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

mkdir /home/travis/.vnc/
ls /home/travis/

tee /home/travis/.vnc/jsql-passwd << EOF
full_password
view_password
EOF

ls /home/travis/.vnc/
cat /home/travis/.vnc/jsql-passwd

#vncpasswd -f /home/travis/.vnc/jsql-passwd > /home/travis/.vnc/passwd

vncpasswd -f > /home/travis/.vnc/passwd <<EOF
full_password
view_password
EOF

vncpasswd -f > "$HOME/.vnc/passwd" <<EOF
full_password
view_password
EOF


vncpasswd << EOF
123456
123456
EOF

cat /home/travis/.vnc/passwd

export OLD_DISPLAY=${DISPLAY}
vncserver ":${NEW_DISPLAY}" -localhost -geometry 1600x1200 -depth 16 &
export DISPLAY=:${NEW_DISPLAY}