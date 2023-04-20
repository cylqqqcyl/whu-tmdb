/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.create.deputyclass.CreateTJoinDeputyClass;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.WithItem;

//TODO TMDB
//因为JSqlParser支持deparser，创建了parser方法之后，还要写一个deparser，参考createDeputyClassDeparser
public class CreateTJoinDeputyClassDeParser extends AbstractDeParser<CreateTJoinDeputyClass> {
    private final SelectVisitor selectVisitor;

    public CreateTJoinDeputyClassDeParser(StringBuilder buffer) {
        super(buffer);
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        this.selectVisitor = selectDeParser;
    }

    public CreateTJoinDeputyClassDeParser(StringBuilder buffer, SelectVisitor selectVisitor) {
        super(buffer);
        this.selectVisitor = selectVisitor;
    }

    public void deParse(CreateTJoinDeputyClass createTJoinDeputyClass) {
        this.buffer.append("CREATE ");
        if (createTJoinDeputyClass.isOrReplace()) {
            this.buffer.append("OR REPLACE ");
        }

        this.buffer.append(createTJoinDeputyClass.getType()+" ").append(createTJoinDeputyClass.getDeputyClass().getFullyQualifiedName());
        this.buffer.append(" AS ");
        Select select = createTJoinDeputyClass.getSelect();
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

        createTJoinDeputyClass.getSelect().getSelectBody().accept(this.selectVisitor);
    }
}
