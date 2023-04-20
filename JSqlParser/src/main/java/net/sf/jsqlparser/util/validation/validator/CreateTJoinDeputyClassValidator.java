/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.validation.validator;

import java.util.Iterator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.create.deputyclass.CreateTJoinDeputyClass;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

/**
 * @author gitmotte
 */
//TODO TMDB
//实现CreateTJoinDeputyClassValidator类，这个类检验创建的CreateTJoinDeputyClass是否符合规则，具体参考CreateDeputyClassValidator
public class CreateTJoinDeputyClassValidator extends AbstractValidator<CreateTJoinDeputyClass> {
    public CreateTJoinDeputyClassValidator() {
    }

    public void validate(CreateTJoinDeputyClass createTJoinDeputyClass) {
        Iterator var2 = this.getCapabilities().iterator();

        while(var2.hasNext()) {
            ValidationCapability c = (ValidationCapability)var2.next();
            this.validateFeature(c, Feature.createSelectDeputy);
            this.validateFeature(c, createTJoinDeputyClass.isOrReplace(), Feature.createOrReplaceView);
            this.validateName(c, NamedObject.view, createTJoinDeputyClass.getDeputyClass().getFullyQualifiedName(), false, new NamedObject[0]);
        }

        SelectValidator v = (SelectValidator)this.getValidator(SelectValidator.class);
        Select select = createTJoinDeputyClass.getSelect();
        if (this.isNotEmpty(select.getWithItemsList())) {
            select.getWithItemsList().forEach((wi) -> {
                wi.accept(v);
            });
        }

        select.getSelectBody().accept(v);
    }

}
