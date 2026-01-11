#!/bin/sh

set -e

retry=0
while ! echo "$result" | grep -q "1337331"; do
    retry=$((retry+1))
    if [ $retry -gt 60 ] ; then
      exit 1
    fi

    # should use hdbsql cli instead though not working
    result=$(echo '
        Class.forName("com.sap.db.jdbc.Driver")
        import java.sql.*;

        StringBuilder result = new StringBuilder();
        try (
            Connection con = DriverManager.getConnection("jdbc:sap://jsql-hana:39017?encrypt=false&validateCertificate=false", "system", "1anaHEXH");
            PreparedStatement pstmt = con.prepareStatement("select 1337330+1 from dummy");
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) result.append(rs.getString(1));
        }
        System.out.println(result);
        /exit
    ' | jshell --class-path "model/src/test/resources/docker/scripts/healthcheck/jdbc/ngdbc-2.27.6.jar" --feedback silent)

    >&2 echo "Hana is unavailable - sleeping #${retry}"
    sleep 5
done

>&2 echo "Hana is up - executing command"
