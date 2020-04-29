#/bin/bash
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

cat << EOF > /home/travis/.vnc/passwd
$full_password
$view_password
EOF

ls /home/travis/.vnc/

vncpasswd /home/travis/.vnc/passwd

export OLD_DISPLAY=${DISPLAY}
vncserver ":${NEW_DISPLAY}" -localhost -geometry 1600x1200 -depth 16
export DISPLAY=:${NEW_DISPLAY}

./src/test/resources/swing/start-vnc.sh: line 20: /home/travis/.vnc/passwd: No such file or directory