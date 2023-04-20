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

package net.sf.jsqlparser.statement.create.deputyclass;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.Select;

public class CreateDeputyClass implements Statement {
    private Table deputyClass;
    private Select select;
    private boolean orReplace = false;
    private String type;

    public CreateDeputyClass() {
    }

    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public Table getDeputyClass() {
        return this.deputyClass;
    }

    public void setDeputyClass(Table deputyClass) {
        this.deputyClass = deputyClass;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOrReplace() {
        return this.orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public Select getSelect() {
        return this.select;
    }

    public void setSelect(Select select) {
        this.select = select;
    }

    public String toString() {
        StringBuilder sql = new StringBuilder("CREATE ");
        if (this.isOrReplace()) {
            sql.append("OR REPLACE ");
        }

        sql.append(this.type + " ");
        sql.append(this.deputyClass);
        sql.append(" AS ").append(this.select);
        return sql.toString();
    }
}
