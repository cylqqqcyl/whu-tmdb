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

package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.create.deputyclass.CreateDeputyClass;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.WithItem;

public class CreateDeputyClassDeParser extends AbstractDeParser<CreateDeputyClass> {
    private final SelectVisitor selectVisitor;

    public CreateDeputyClassDeParser(StringBuilder buffer) {
        super(buffer);
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        this.selectVisitor = selectDeParser;
    }

    public CreateDeputyClassDeParser(StringBuilder buffer, SelectVisitor selectVisitor) {
        super(buffer);
        this.selectVisitor = selectVisitor;
    }

    public void deParse(CreateDeputyClass createDeputyClass) {
        this.buffer.append("CREATE ");
        if (createDeputyClass.isOrReplace()) {
            this.buffer.append("OR REPLACE ");
        }

        this.buffer.append(createDeputyClass.getType() + " ").append(createDeputyClass.getDeputyClass().getFullyQualifiedName());
        this.buffer.append(" AS ");
        Select select = createDeputyClass.getSelect();
        if (select.getWithItemsList() != null) {
            this.buffer.append("WITH ");
            boolean first = true;

            WithItem item;
            for(Iterator var4 = select.getWithItemsList().iterator(); var4.hasNext(); item.accept(this.selectVisitor)) {
                item = (WithItem)var4.next();
                if (!first) {
                    this.buffer.append(", ");
                } else {
                    first = false;
                }
            }

            this.buffer.append(" ");
        }

        createDeputyClass.getSelect().getSelectBody().accept(this.selectVisitor);
    }
}
