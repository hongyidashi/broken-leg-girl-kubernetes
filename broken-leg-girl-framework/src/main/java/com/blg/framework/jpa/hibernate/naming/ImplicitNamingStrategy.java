package com.blg.framework.jpa.hibernate.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.spi.MetadataBuildingContext;

/**
 * 默认名字策略
 * @author lujijiang
 */
public class ImplicitNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

    private String fixName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        char[] cs = name.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            if(c=='$'){
                continue;
            }
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(cs[i - 1])) {
                nameBuilder.append("_");
                nameBuilder.append(Character.toLowerCase(c));
            } else {
                nameBuilder.append(Character.toLowerCase(c));
            }
        }
        if (nameBuilder.charAt(nameBuilder.length() - 1) != '_') {
            nameBuilder.append("_");
        }
        name = nameBuilder.toString();
        return name;
    }

    @Override
    protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
        return super.toIdentifier(fixName(stringForm), buildingContext);
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        final String name = source.getOwningPhysicalTableName() + source.getNonOwningPhysicalTableName();
        return toIdentifier(name, source.getBuildingContext());
    }

}
