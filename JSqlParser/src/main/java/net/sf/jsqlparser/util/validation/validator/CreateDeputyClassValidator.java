/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2023 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.sf.jsqlparser.util.validation.validator;

import java.util.Iterator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.create.deputyclass.CreateDeputyClass;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

public class CreateDeputyClassValidator extends AbstractValidator<CreateDeputyClass> {
    public CreateDeputyClassValidator() {
    }

    public void validate(CreateDeputyClass createDeputyClass) {
        Iterator var2 = this.getCapabilities().iterator();

        while(var2.hasNext()) {
            ValidationCapability c = (ValidationCapability)var2.next();
            this.validateFeature(c, Feature.createSelectDeputy);
            this.validateFeature(c, createDeputyClass.isOrReplace(), Feature.createOrReplaceView);
            this.validateName(c, NamedObject.view, createDeputyClass.getDeputyClass().getFullyQualifiedName(), false, new NamedObject[0]);
        }

        SelectValidator v = (SelectValidator)this.getValidator(SelectValidator.class);
        Select select = createDeputyClass.getSelect();
        if (this.isNotEmpty(select.getWithItemsList())) {
            select.getWithItemsList().forEach((wi) -> {
                wi.accept(v);
            });
        }

        select.getSelectBody().accept(v);
    }
}
