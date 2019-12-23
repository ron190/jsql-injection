package com.jsql.util.tampering;

import org.yaml.snakeyaml.Yaml;

import com.jsql.util.tampering.yaml.ModelYaml;

public class TamperingYaml {
    
    private ModelYaml modelYaml;

    public TamperingYaml(String fileYaml) {
        Yaml yaml = new Yaml();
        
        this.modelYaml = yaml.loadAs(TamperingYaml.class.getClassLoader().getResourceAsStream("tamper/"+ fileYaml), ModelYaml.class);
    }

    public ModelYaml getModelYaml() {
        return this.modelYaml;
    }

}
