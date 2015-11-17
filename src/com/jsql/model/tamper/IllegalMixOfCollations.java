package com.jsql.model.tamper;

public class IllegalMixOfCollations extends IEncloseSelect {
    public IllegalMixOfCollations() {
        super.setLead("CONVERT(");
        super.setTrail("USING+utf8)COLLATE+utf8_general_ci");
    }
}
